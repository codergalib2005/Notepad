package com.edureminder.easynotes.presentation.screen.diary_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Scaffold
import com.edureminder.easynotes.R
import java.io.File

@Composable
fun AddDiaryScreen(navController: NavHostController, onPDFGenerate: File) {
    Scaffold {
        Column {
            Column {
                Row {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = null
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_icon_dark),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}