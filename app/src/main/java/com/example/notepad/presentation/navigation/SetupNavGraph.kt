package com.example.notepad.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notepad.presentation.screen.edit_note_screen.EditNoteScreen
import com.example.notepad.presentation.screen.main_screen.MainScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: Screen,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable <Screen.MainScreen>{
            MainScreen(navController = navController)
        }

        composable<Screen.EditNoteScreen>(
            enterTransition = { slideLeftEnter() },
            exitTransition = { slideLeftExit() },
            popEnterTransition = { slideRightEnter() },
            popExitTransition = { slideRightExit() }
        ) {
            EditNoteScreen(navController = navController)
        }
    }
}