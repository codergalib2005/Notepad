package com.example.notepad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.notepad.presentation.navigation.Screen
import com.example.notepad.presentation.navigation.SetupNavGraph
import com.example.notepad.ui.theme.NotepadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NotepadTheme {
                SetupNavGraph (
                    navController,
                    startDestination = Screen.MainScreen
                )
            }
        }
    }
}
