package com.edureminder.notebook.presentation.screen.edit_note.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.room.note.NoteViewModel
import com.edureminder.notebook.room.note.Status
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.work.note.cancelScheduledExactNoteWorkersIfExist

@Composable
fun NoteDeletePopup(
    editorViewModel: NoteEditorViewModel,
    notesViewModel: NoteViewModel,
    ids: List<String>,
    onEmptySelectedNotes: () -> Unit,
    onSnackbarUpdate: (String) -> Unit,
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = {
            editorViewModel.isDeletePopupOpen = false
        }
    ) {
        NoteDeleteTrashPopup(
            ids = ids,
            onDismissPopup = {
                editorViewModel.isDeletePopupOpen = false
                onEmptySelectedNotes()
            },
            onDeleteNotes = {
                if (ids.isNotEmpty()) {
                    notesViewModel.updateStatusesForNotes(ids, Status.DELETED)
                    cancelScheduledExactNoteWorkersIfExist(context, ids)
                    onSnackbarUpdate("Deleted ${ids.size} note${if (ids.size > 1) "s" else ""}")
                }
                onEmptySelectedNotes()
            }
        )
    }
}
@Composable
fun NoteDeleteTrashPopup(
    ids: List<String>,
    onDismissPopup: () -> Unit,
    onDeleteNotes: () -> Unit
){
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column (
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        onDismissPopup()
                    },
                    modifier = Modifier
                        .size(25.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestoreFromTrash,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(
                        text = if(ids.size > 1) "Delete Notes" else "Delete Note",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    buildAnnotatedString {
                        append("Do you want to delete this note?\n")
                        append("Deleted items will be in the ")
                        withStyle (
                            style = SpanStyle(
                                fontWeight = FontWeight.Medium,
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Trash ")
                        }
                        append("folder")
                    },
                    modifier = Modifier
                        .padding(5.dp),
                    fontSize = 15.sp,
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onDismissPopup()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary
                        )
                    }
                    Button(
                        onClick = {
                            onDeleteNotes()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}