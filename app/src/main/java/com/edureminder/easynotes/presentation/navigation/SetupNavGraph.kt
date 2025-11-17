package com.edureminder.easynotes.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edureminder.easynotes.drive.AuthViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.AddNoteScreen
import com.edureminder.easynotes.presentation.screen.edit_note.EditNoteScreen
import com.edureminder.easynotes.presentation.screen.main_screen.MainScreen
import java.io.File

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    onPDFGenerate: File
) {
    val context = LocalContext.current
    val  authViewModel: AuthViewModel = hiltViewModel()


    LaunchedEffect(context) {
        authViewModel.initializeGoogleSignInClient(context)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable <Screen.MainScreen>{
            MainScreen(
                navController = navController,
                authViewModel
            )
        }

        composable<Screen.EditNoteScreen>(
            enterTransition = { slideLeftEnter() },
            exitTransition = { slideLeftExit() },
            popEnterTransition = { slideRightEnter() },
            popExitTransition = { slideRightExit() }
        ) {
            val arg = it.toRoute<Screen.EditNoteScreen>()
            EditNoteScreen(
                navController = navController,
                noteID = arg.noteId,
                onPDFGenerate = onPDFGenerate
            )
        }

        composable<Screen.AddNoteScreen> (
            enterTransition = { slideRightEnter() },
            exitTransition = { slideRightExit() },
            popEnterTransition = { slideLeftEnter() },
            popExitTransition = { slideLeftExit() }
        ){
            AddNoteScreen(
                navController,
                onPDFGenerate = onPDFGenerate
            )
        }
    }
}