package com.edureminder.easynotes.presentation.screen.main_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.drive.AuthViewModel
import com.edureminder.easynotes.presentation.components.drawer.CustomDrawer
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.main_screen.diary_views.DiaryView
import com.edureminder.easynotes.presentation.screen.main_screen.note_views.NoteView
import com.edureminder.easynotes.ui.Container
import com.edureminder.easynotes.ui.mode.ModeViewModel
import kotlinx.coroutines.launch

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun MainScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val scope = rememberCoroutineScope()
    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }
    val tasksTypes = listOf(
        TaskTypeItem(
            1,
            name = "Task",
            icon = R.drawable.task,
            onClick = {
                navController.navigate(Screen.AddNoteScreen)
            }
        ),
        TaskTypeItem(
            2,
            name = "Diary",
            icon = R.drawable.diary,
            onClick = {
                navController.navigate(Screen.AddDiaryScreen)
            }
        ),
        TaskTypeItem(
            3,
            name = "Notes",
            icon = R.drawable.note,
            onClick = {
                navController.navigate(Screen.AddNoteScreen)
            }
        )
    )

    val context = LocalContext.current
    val modeViewModel = ModeViewModel(context)
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val isDarkMode by modeViewModel.isDarkTheme.collectAsState()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                modifier = Modifier
                    .width(280.dp),
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                CustomDrawer(
                    onCloseClick = {
                        scope.launch {
                            drawerState.apply {
                                close()
                            }
                        }
                    },
                    navController = navController,
                    isDarkMode
                )
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FabMenu(
                    tasksTypes
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Container,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                    0 -> NoteView(
                            navController,
                            onSnackbarUpdate = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            },
                            onToggleSidebar = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                    )

                    3 -> DiaryView(
                            navController,
                            onSnackbarUpdate = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            },
                            onToggleSidebar = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    BottomNavigationBar(
                        navController,
                        selectedTab,
                        onTabSelected = {
                            selectedTab = it
                        }
                    )
                }
            }
        }
    }
}