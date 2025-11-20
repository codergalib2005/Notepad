package com.edureminder.easynotes.presentation.screen.main_screen.note_views

import android.text.Html
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.edit_note.ChecklistItem
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.room.note.Type
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.utils.convertTo12HourFormat
import com.edureminder.easynotes.utils.toDashDateTime
import kotlinx.serialization.json.Json

data class MoreOption(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesItem(
    note: Note,
    navHostController: NavHostController,
    selectedNotes: MutableState<List<Note>>,
    notesViewModel: NoteViewModel,
    folderMap: Map<String, Folder>,
    editorViewModel: NoteEditorViewModel,
    onSnackbarUpdate: (String) -> Unit,
    is24HourClock: Boolean
) {
//    val tag = tagList.find { it.id.toString() == note.tag }
    val folderColor = folderMap[note.folderId]?.color
        ?.toColorInt()
        ?.let { Color(it) }
        ?: Primary

    var isMoreOptionsOpen by remember { mutableStateOf(false) }

    val options = listOf(
        MoreOption(
            id = 1,
            name = "Select",
            icon = Icons.Default.CheckCircleOutline,
            onClick = {
                selectedNotes.value += note
            }
        ),
        MoreOption(
            id = 2,
            name = if(note.isFavourite) "Unpin" else "Pin",
            icon = if(note.isFavourite) Icons.Filled.PushPin else Icons.Outlined.PushPin,
            onClick = {
                if(note.isFavourite) {
                    notesViewModel.unpinNote(note.id)
                    onSnackbarUpdate("Note unpinned")
                } else {
                    notesViewModel.pinNote(note.id)
                    onSnackbarUpdate("Note pinned")
                }
            }
        ),
        MoreOption(
            id = 3,
            name = "Move",
            icon = Icons.Default.DragIndicator,
            onClick = {
                selectedNotes.value += note
                editorViewModel.isOpenFolderList = true
            }
        ),
        MoreOption(
            id = 4,
            name = if(note.isLocked) "Unlock" else "Lock",
            icon = if(note.isLocked) Icons.Filled.Lock else Icons.Outlined.LockOpen,
            onClick = {
                if(note.isLocked) {
                    notesViewModel.unlockNote(note.id)
                    onSnackbarUpdate("Note unlocked")
                } else {
                    notesViewModel.lockNote(note.id)
                    onSnackbarUpdate("Note locked")
                }
            }
        ),
//        MoreOption(
//            id = 5,
//            name = "Share",
//            icon = Icons.Default.Share,
//            onClick = {
//
//            }
//        ),
        MoreOption(
            id = 6,
            name = "Delete",
            icon = Icons.Default.Delete,
            onClick = {
                selectedNotes.value += note
                editorViewModel.isDeletePopupOpen = true
            }
        )
    )

    Box (
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .background(Color.White)
            .background(folderColor)
            .border(
                width = if (selectedNotes.value.contains(note)) 2.dp else 0.dp,
                color = if(selectedNotes.value.contains(note)) Primary else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall
            )
            .combinedClickable(
                onClick = {
                    if (selectedNotes.value.isEmpty()) {
                        note.id.let {
                            if(note.type == Type.NOTE) {
                                navHostController.navigate(Screen.EditNoteScreen(it))
                            } else {
                                navHostController.navigate(Screen.EditChecklistScreen(it))
                            }
                        }
                    } else {
                        if(selectedNotes.value.contains(note)) {
                            selectedNotes.value -= note
                        } else {
                            selectedNotes.value += note
                        }
                    }
                },
                onLongClick = {
                    selectedNotes.value += note
                },
            ),
    ) {
        Row (
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxSize()
                .background(Color.White)
                .background(folderColor.copy(0.2f)),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box (
                modifier = Modifier
                    .fillMaxHeight()
                    .width(40.dp)
                    .padding(top = 3.dp),
            ) {
                if(selectedNotes.value.isNotEmpty()){
                    Box (
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = Primary,
                                    shape = CircleShape
                                )
                        ){
                            if(selectedNotes.value.contains(note)) {
                                Spacer(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            Primary
                                        )
                                )
                            }
                        }
                    }
                } else {
                    if (note.type == Type.CHECKLIST) {
                        Image(
                            painter = painterResource(R.drawable.list),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp),
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.note),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp),
                        )
                    }
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (note.title.isEmpty()) "Empty Title" else note.title.replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                    )
                    if(selectedNotes.value.isEmpty()){
                        Column {
                            Box (
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(
                                    ){
                                        isMoreOptionsOpen = true
                                    }
                                    .padding(horizontal = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreHoriz,
                                    contentDescription = null
                                )
                            }
                            DropdownMenu(
                                expanded = isMoreOptionsOpen,
                                onDismissRequest = { isMoreOptionsOpen = false },
                                modifier = Modifier
                                    .width(160.dp),
                                containerColor = Color.White,
                                shadowElevation = 0.dp,
                            ) {
                                options.forEachIndexed {index, item ->
                                    Column {
                                        if(options.size == index+1) {
                                            HorizontalDivider()
                                        }
                                        DropdownMenuItem(
                                            contentPadding = PaddingValues(0.dp),
                                            onClick = {
                                                isMoreOptionsOpen = false
                                                item.onClick()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .semantics {
                                                    contentDescription = item.name
                                                },
                                            text = {
                                                Row (
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = item.name,
                                                        tint = if(options.size == index+1) Color.Red else Color.Black.copy(0.6f),
                                                        modifier = Modifier
                                                            .size(19.dp)
                                                            .rotate(
                                                                when(item.id){
                                                                    2 -> 45f
                                                                    3 -> 90f
                                                                    else -> 0f
                                                                }
                                                            )
                                                    )
                                                    Text(
                                                        text = item.name,
                                                        color =  if(options.size == index+1) Color.Red else Color.Black.copy(0.6f),
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 14.sp,
                                                        lineHeight = 14.sp
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Column (
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    if(note.type == Type.NOTE){
                        Text(
                            text = if (note.body.isEmpty()) {
                                "Empty Body"
                            } else {
                                // Clean the HTML tags and get the plain text
                                val plainText = Html.fromHtml(note.body, Html.FROM_HTML_MODE_LEGACY).toString().trim()
                                // Limit to 100 characters
                                plainText.take(140).takeIf { it.isNotEmpty() } ?: "Empty Body"
                            },
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black.copy(0.8f),
                            lineHeight = 13.sp
                        )
                    } else {
//                val checklistItems = Json.decodeFromString<List<ChecklistItem>>(note.body)
                        val stringFromDb = note.body
                        val checklistItems = Json.decodeFromString<List<ChecklistItem>>(stringFromDb)

                        checklistItems
                            .take(3)
                            .filter { it.title.isNotBlank() } // 👈 Skip items with empty titles
                            .forEach { checkListItem ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(11.dp),
                                        tint = if (checkListItem.isCompleted) Primary else Color.Gray
                                    )
                                    Text(
                                        text = checkListItem.title,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textDecoration = if (checkListItem.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (checkListItem.isCompleted) Color.Gray else Color.Black
                                    )
                                }
                            }
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                ) {
                    if(note.reminderType == 2){
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(14.dp)
                            )
                            Text(
                                text =  convertTo12HourFormat(note.reminderTime, is24HourClock),
                                fontSize = 11.sp)
                        }
                    }
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(14.dp)
                        )
                        Text(text = note.updatedAt.toDashDateTime(), fontSize = 11.sp)
                    }

                }
            }
        }
    }
}