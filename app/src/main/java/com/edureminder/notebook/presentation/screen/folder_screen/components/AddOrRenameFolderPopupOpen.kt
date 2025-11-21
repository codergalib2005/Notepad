package com.edureminder.notebook.presentation.screen.folder_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.presentation.screen.edit_note.folderColors
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.folder.FolderViewModel
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.viewmodels.MainViewModel

@Composable
fun AddOrRenameFolderPopupOpen(
    noteEditorViewModel: NoteEditorViewModel,
    folderViewModel: FolderViewModel,
    mainViewModel: MainViewModel,
    navController: NavController,
    onDismissRequest: () -> Unit = {},
){
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val isPro by mainViewModel.isPro.collectAsState()

    Dialog(
        onDismissRequest = {
            noteEditorViewModel.isAddOrRenameFolderPopupOpen = false
            noteEditorViewModel.folderBeingEdited = null
            noteEditorViewModel.folderName = ""
            noteEditorViewModel.selectedColor = folderColors.first()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.size(30.dp))
                Text(
                    text = if (noteEditorViewModel.folderBeingEdited == null) "Create Folder" else "Edit Folder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                IconButton(
                    onClick = {
                        noteEditorViewModel.isAddOrRenameFolderPopupOpen = false
                        noteEditorViewModel.folderBeingEdited = null
                        noteEditorViewModel.folderName = ""
                        noteEditorViewModel.selectedColor = folderColors.first()
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Black.copy(0.7f)
                    )
                }
            }

            Spacer(Modifier.height(5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black.copy(0.1f), shape = RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = noteEditorViewModel.folderName,
                    onValueChange = {
                        if (it.length <= 30) noteEditorViewModel.folderName = it
                    },
                    singleLine = true,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp, bottom = 4.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        "${noteEditorViewModel.folderName.length}/30",
                        color = if (noteEditorViewModel.folderName.length >= 30) Color.Red else Color.Black,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                folderColors.forEach { item ->
                    Spacer(
                        modifier = Modifier
                            .size(30.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if(noteEditorViewModel.selectedColor == item) Primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(Color(item.toColorInt()))
                            .clickable {
                                noteEditorViewModel.selectedColor = item
                            }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        noteEditorViewModel.isAddOrRenameFolderPopupOpen = false
                        noteEditorViewModel.folderBeingEdited = null
                        noteEditorViewModel.folderName = ""
                        noteEditorViewModel.selectedColor = folderColors.first()
                    },
                    modifier = Modifier
                        .weight(1f),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black.copy(0.7f)
                    )
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (noteEditorViewModel.folderName.isNotBlank()) {
                            if (noteEditorViewModel.folderBeingEdited == null) {
                                if (!isPro && folders.size >= 5) {
                                    // Navigate to subscribe screen instead of creating a folder
                                    noteEditorViewModel.isAddOrRenameFolderPopupOpen = false
                                    onDismissRequest() // close dialog first
                                    // Navigate to subscribe screen
                                    navController.navigate(Screen.Subscription)
                                    return@Button
                                }

                                val folder = Folder(
                                    name = noteEditorViewModel.folderName.trim(),
                                    color = noteEditorViewModel.selectedColor,
                                )
                                folderViewModel.upsertFolder(folder)
                            } else {
                                noteEditorViewModel.folderBeingEdited?.let {
                                    val updatedFolder = it.copy(
                                        name = noteEditorViewModel.folderName.trim(),
                                        color = noteEditorViewModel.selectedColor
                                    )
                                    folderViewModel.upsertFolder(updatedFolder)
                                }
                            }

                            noteEditorViewModel.isAddOrRenameFolderPopupOpen = false
                            noteEditorViewModel.folderBeingEdited = null
                            noteEditorViewModel.folderName = ""
                            noteEditorViewModel.selectedColor = folderColors.first()
                            onDismissRequest()
                        }
                    },
                    enabled = noteEditorViewModel.folderName.isNotBlank(),
                    modifier = Modifier
                        .width(150.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Primary.copy(0.8f),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(if (noteEditorViewModel.folderBeingEdited == null) "Add" else "Rename")
                }
            }
        }
    }
}