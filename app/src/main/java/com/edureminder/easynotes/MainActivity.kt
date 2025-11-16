package com.edureminder.easynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.navigation.SetupNavGraph
import com.edureminder.easynotes.ui.NotepadTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive =  true
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()



            SetupNavGraph (
                navController,
                startDestination = Screen.MainScreen,
                onPDFGenerate = getDirectory()
            )
        }
    }

    fun getDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

}
