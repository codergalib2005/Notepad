package com.edureminder.easynotes.drive

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edureminder.easynotes.room.folder.FolderRepository
import com.edureminder.easynotes.room.note.NoteRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {
    // Auth state flow
    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState: StateFlow<AuthState> = _authState

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> = _isDeletingAccount


    private var googleSignInClient: GoogleSignInClient? = null

    fun initializeGoogleSignInClient(
        context: Context,
        webClientId: String = "751780926252-gv9eaon4jm9kgcgi0v7vgqjqcofliuau.apps.googleusercontent.com"
    ) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(webClientId)
            .requestServerAuthCode(webClientId)
            .requestScopes(
                Scope("https://www.googleapis.com/auth/drive.file")
            )
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null && account.grantedScopes.contains(
                Scope("https://www.googleapis.com/auth/drive.file")
            )
        ) {
            _authState.value = AuthState.SignedIn(account)
            Log.d("Log1", "Already signed in: ${account.displayName}")
        } else {
            _authState.value = AuthState.SignedOut
        }
    }

    fun signIn(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        googleSignInClient?.let {
            val signInIntent = it.signInIntent
            launcher.launch(signInIntent)
        } ?: run {
            Log.e("Log1", "GoogleSignInClient not initialized.")
            _authState.value = AuthState.Error("Google Sign-In client not initialized.")
        }
    }

    fun handleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                _authState.value = AuthState.SignedIn(account)
                Log.d("Log1", "Sign-in successful for: ${account}")
            } catch (e: ApiException) {
                Log.e("Log1", "Google Sign-In failed: ${e}")
                _authState.value = AuthState.Error("Sign-in failed: ${e}")
            }
        } else {
            Log.e("Log1", "Sign-in canceled or failed. resultCode=$result")
            _authState.value = AuthState.Error("Sign-in canceled or failed.")
        }
    }


    fun signOut() {
        googleSignInClient?.signOut()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.SignedOut
                Log.d("Log1", "Signed out successfully.")
            } else {
                Log.e("Log1", "Error signing out.")
                _authState.value = AuthState.Error("Error signing out.")
            }
        } ?: run {
            Log.e("Log1", "GoogleSignInClient not initialized for signOut.")
        }
    }

    sealed class AuthState {
        object SignedOut : AuthState()
        data class SignedIn(val account: GoogleSignInAccount) : AuthState()
        data class Error(val message: String) : AuthState()
        object Loading : AuthState()
    }

    fun deleteAccount(context: Context, account: GoogleSignInAccount, onComplete: () -> Unit) {
        val driveSyncManager = DriveSyncManager(context, noteRepository, folderRepository)

        viewModelScope.launch {
            _isDeletingAccount.value = true // Start loading

            try {
                // Move blocking Drive call to IO thread
                withContext(Dispatchers.IO) {
                    driveSyncManager.deleteUserDataFromDrive(account)
                }

                Log.d("Log1", "User data deleted from Drive.")

                // Safe to update state on Main thread
                googleSignInClient?.revokeAccess()?.addOnCompleteListener { task ->
                    _isDeletingAccount.value = false

                    if (task.isSuccessful) {
                        _authState.value = AuthState.SignedOut
                        Log.d("Log1", "Access revoked and signed out.")
                    } else {
                        Log.e("Log1", "Failed to revoke access.")
                        _authState.value = AuthState.Error("Failed to revoke access.")
                    }

                    onComplete()
                }

            } catch (e: Exception) {
                _isDeletingAccount.value = false
                Log.e("Log1", "Error deleting user data: ${e.message}")
                _authState.value = AuthState.Error("Failed to delete backup: ${e.message}")
                onComplete()
            }
        }
    }


}