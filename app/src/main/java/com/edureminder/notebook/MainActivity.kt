package com.edureminder.notebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.navigation.SetupNavGraph
import com.edureminder.notebook.room.folder.FolderViewModel
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


        val folderViewModel: FolderViewModel by viewModels()
        folderViewModel.insertDummyDataIfEmpty()

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
