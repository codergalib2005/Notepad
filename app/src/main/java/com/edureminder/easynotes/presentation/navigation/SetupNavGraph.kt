package com.edureminder.easynotes.presentation.navigation


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.edureminder.easynotes.drive.AuthViewModel
import com.edureminder.easynotes.presentation.screen.checklist_screen.AddChecklistScreen
import com.edureminder.easynotes.presentation.screen.checklist_screen.EditChecklistScreen
import com.edureminder.easynotes.presentation.screen.diary_screen.AddDiaryScreen
import com.edureminder.easynotes.presentation.screen.diary_screen.EditDiaryScreen
import com.edureminder.easynotes.presentation.screen.edit_note.AddNoteScreen
import com.edureminder.easynotes.presentation.screen.edit_note.EditNoteScreen
import com.edureminder.easynotes.presentation.screen.image_view_screen.ImageViewScreen
import com.edureminder.easynotes.presentation.screen.main_screen.MainScreen
import java.io.File


@OptIn(ExperimentalSharedTransitionApi::class)
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
    SharedTransitionLayout {

        NavHost(
            navController = navController,
            startDestination = Screen.MainScreen
        ) {
            composable<Screen.MainScreen> {
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


            composable<Screen.AddNoteScreen>(
                enterTransition = { slideRightEnter() },
                exitTransition = { slideRightExit() },
                popEnterTransition = { slideLeftEnter() },
                popExitTransition = { slideLeftExit() }
            ) {
                AddNoteScreen(
                    navController,
                    onPDFGenerate = onPDFGenerate
                )
            }

            composable<Screen.AddDiaryScreen>(
                enterTransition = { slideRightEnter() },
                exitTransition = { slideRightExit() },
                popEnterTransition = { slideLeftEnter() },
                popExitTransition = { slideLeftExit() }
            ) {
                AddDiaryScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    navController,
                    onPDFGenerate = onPDFGenerate
                )
            }

            composable<Screen.EditDiaryScreen>(
                enterTransition = { slideLeftEnter() },
                exitTransition = { slideLeftExit() },
                popEnterTransition = { slideRightEnter() },
                popExitTransition = { slideRightExit() }
            ) {
                val arg = it.toRoute<Screen.EditDiaryScreen>()
                EditDiaryScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    navController = navController,
                    diaryID = arg.diaryId,
                    onPDFGenerate = onPDFGenerate
                )
            }

            composable <Screen.AddChecklistScreen>{
                AddChecklistScreen(
                    navController,
                    onPDFGenerate = onPDFGenerate
                )
            }
            composable <Screen.EditChecklistScreen>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "checklists://checklist/{checklistId}"
                        action = android.content.Intent.ACTION_VIEW
                    }
                )
            ){
                val arg = it.toRoute<Screen.EditChecklistScreen>()
                EditChecklistScreen(
                    navController,
                    checklistId = arg.checklistId,
                    onPDFGenerate = onPDFGenerate
                )
            }

            composable<Screen.ImageViewScreen> {
                val arg = it.toRoute<Screen.ImageViewScreen>()
                ImageViewScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    imageUrl = arg.imageUrl,
                    navController = navController
                )
            }
        }
    }
}