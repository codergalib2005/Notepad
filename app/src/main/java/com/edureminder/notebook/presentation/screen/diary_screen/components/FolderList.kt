package com.edureminder.notebook.presentation.screen.diary_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.ui.Primary

@Composable
fun FolderList(
    onTagSelected: (Folder) -> Unit,
    folders: List<Folder>,
    editorViewModel: NoteEditorViewModel
) {
    Dialog(
        onDismissRequest = {
            editorViewModel.isOpenFolderList = false
        }
    ) {
        Card (
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.size(30.dp))
                    Text(
                        text = "Folders",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        color = Primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                    IconButton(
                        onClick = {
                            editorViewModel.isOpenFolderList = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Primary,
                        )
                    }
                }
                HorizontalDivider(
                    color = Primary.copy(0.8f),
                    thickness = 2.dp
                )
                LazyColumn (
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    items(folders) { folder ->
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = if(folder.id == "none") false else true,
                                ){
                                    onTagSelected(folder)
                                }
                                .padding(horizontal = 15.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(
                                Modifier
                                    .clip(CircleShape)
                                    .size(20.dp)
                                    .background(Color(folder.color.toColorInt()))
                            )
                            Text(
                                text = folder.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Column {
                    HorizontalDivider(color = Primary.copy(0.4f))
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .clickable {
                                editorViewModel.isOpenFolderList = false
                                /**
                                 *
                                 */
                                editorViewModel.isAddOrRenameFolderPopupOpen = true
                                editorViewModel.folderBeingEdited = null
                                editorViewModel.folderName = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "New Folder",
                            fontWeight = FontWeight.Medium,
                            color = Primary,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}