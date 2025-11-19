package com.edureminder.easynotes.presentation.screen.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.drive.AuthViewModel
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.main_screen.diary_views.DiaryView
import com.edureminder.easynotes.ui.Container

@Composable
fun MainScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    var selectedTab by rememberSaveable {
        mutableIntStateOf(3)
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
                navController.navigate(Screen.EditNoteScreen)
            }
        )
    )

    Scaffold(
        floatingActionButton = {
            FabMenu(
                tasksTypes
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Container
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when(selectedTab){
                3 -> DiaryView(navController)
            }

            Box (
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