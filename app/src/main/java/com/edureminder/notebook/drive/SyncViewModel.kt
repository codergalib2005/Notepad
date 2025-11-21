package com.edureminder.notebook.drive

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.edureminder.notebook.room.folder.FolderRepository
import com.edureminder.notebook.room.note.NoteRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.text.SimpleDateFormat
import java.util.*

class BackupPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)

    fun saveBackupTime() {
        val formatter = SimpleDateFormat("hh:mm a - dd/MM/yyyy", Locale.getDefault())
        val formattedTime = formatter.format(Date())
        prefs.edit().putString("last_backup_time", formattedTime).apply()
    }

    fun getLastBackupTime(): String? {
        return prefs.getString("last_backup_time", null)
    }
}


@HiltViewModel
class SyncViewModel @Inject constructor(
    application: Application,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val context = application.applicationContext
    private val backupPrefs = BackupPrefs(context)

    private val driveSyncManager by lazy {
        DriveSyncManager(context, noteRepository, folderRepository)
    }

    private val _isBackingUp = MutableStateFlow(false)
    val isBackingUp: StateFlow<Boolean> = _isBackingUp

    private val _isRestoring = MutableStateFlow(false)
    val isRestoring: StateFlow<Boolean> = _isRestoring

    val isAnyLoading: StateFlow<Boolean> = combine(_isBackingUp, _isRestoring) { backup, restore ->
        backup || restore
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)


    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage

    private val _lastBackupTime = MutableStateFlow<String?>(backupPrefs.getLastBackupTime())
    val lastBackupTime: StateFlow<String?> = _lastBackupTime


    fun backupToDrive(account: GoogleSignInAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            _isBackingUp.value = true
            try {
                driveSyncManager.uploadNotesBackup(account)
                backupPrefs.saveBackupTime()
                _lastBackupTime.value = backupPrefs.getLastBackupTime()
                _operationMessage.value = "Backup completed successfully!"
            } catch (e: Exception) {
                e.printStackTrace()
                _operationMessage.value = "Backup failed: ${e.localizedMessage}"
            } finally {
                _isBackingUp.value = false
            }
        }
    }

    fun restoreFromDrive(account: GoogleSignInAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRestoring.value = true
            try {
                driveSyncManager.restoreBackup(account)
                _operationMessage.value = "Restore completed successfully!"
            } catch (e: Exception) {
                e.printStackTrace()
                _operationMessage.value = "Restore failed: ${e.localizedMessage}"
            } finally {
                _isRestoring.value = false
            }
        }
    }


    fun startBackupWork() {
        val workManager = WorkManager.getInstance(context)
        val backupRequest = OneTimeWorkRequestBuilder<BackupWorker>().build()
        workManager.enqueue(backupRequest)
    }

//    fun startRestoreWork() {
//        val workManager = WorkManager.getInstance(context)
//        val restoreRequest = OneTimeWorkRequestBuilder<RestoreWorker>().build()
//        workManager.enqueue(restoreRequest)
//    }


    fun clearMessage() {
        _operationMessage.value = null
    }
}