package com.edureminder.notebook.presentation.screen.main_screen.note_views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.edureminder.notebook.R
import com.edureminder.notebook.preferences.notes.ViewType
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.screen.edit_note.ChecklistItem
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.note.Note
import com.edureminder.notebook.room.note.NoteViewModel
import com.edureminder.notebook.room.note.Type
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.utils.convertTo12HourFormat
import com.edureminder.notebook.utils.toDashDateTime
import kotlinx.serialization.json.Json
import kotlin.math.max
import kotlin.math.min


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesList(
    notesViewModel: NoteViewModel,
    navController: NavHostController,
    state: LazyListState,
    notesListFlow: List<Note>,
    searchText: State<String>,
    viewType: ViewType,
    selectedNotes: MutableState<List<Note>>,
    folderMap: Map<String, Folder>,
    editorViewModel: NoteEditorViewModel,
    onSnackbarUpdate: (String) -> Unit,
    is24HourClock: Boolean,
) {
    val regex = Regex(searchText.value, RegexOption.IGNORE_CASE)
    val searchList = if(searchText.value.isEmpty()) {
        notesListFlow
    } else {
        notesListFlow.filter {
            regex.containsMatchIn(it.title.toString()) || regex.containsMatchIn(it.body.toString())
        }
    }

    // Group and sort notes
    val groupedList = searchList.groupBy { it.isFavourite }.toSortedMap(compareByDescending { it })
    if(viewType == ViewType.GRID) {
        GridList(
            groupedList,
            notesViewModel,
            navController,
            selectedNotes,
            folderMap,
            editorViewModel,
            onSnackbarUpdate,
            is24HourClock
        )
    } else {
        VerticalList(
            state,
            groupedList,
            notesViewModel,
            navController,
            selectedNotes,
            folderMap,
            editorViewModel,
            onSnackbarUpdate,
            is24HourClock
        )
    }
}




@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VerticalList(
    state: LazyListState,
    sortedGroupedNotes: Map<Boolean?, List<Note>>,
    notesViewModel: NoteViewModel,
    navController: NavHostController,
    selectedNotes: MutableState<List<Note>>,
    folderMap: Map<String, Folder>,
    editorViewModel: NoteEditorViewModel,
    onSnackbarUpdate: (String) -> Unit,
    is24HourClock: Boolean,
) {

    LazyColumn(
        state = state,
        modifier = Modifier
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 60.dp, top = 10.dp)
    ) {
        sortedGroupedNotes.forEach { (fav, notes) ->
            stickyHeader {
                if (fav!!) {
                    NotesHeader(text = "Favourites") {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .rotate(35f)
                                .size(32.dp)
                        )
                    }
                } else {
                    NotesHeader(text = "Notes") {
                        Icon(
                            painter = painterResource(id = R.drawable.note),
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            items(notes, key = { it.id }) { item ->
                NotesItem(
                    note = item,
                    navHostController = navController,
                    selectedNotes,
                    notesViewModel,
                    folderMap,
                    editorViewModel,
                    onSnackbarUpdate,
                    is24HourClock
                )
            }
        }
    }

}
@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridList(
    sortedGroupedNotes: Map<Boolean?, List<Note>>,
    notesViewModel: NoteViewModel,
    navController: NavHostController,
    selectedNotes: MutableState<List<Note>>,
    folderMap: Map<String, Folder>,
    editorViewModel: NoteEditorViewModel,
    onSnackbarUpdate: (String) -> Unit,
    is24HourClock: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val maxWidth = 1100  // Maximum size for grid
    val columnWidth = 175 // Every 175dp adds one more column
    val columns = max(2, min(screenWidthDp / columnWidth, maxWidth / columnWidth))

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            bottom = 68.dp,
            top = 10.dp
        )
    ) {
        sortedGroupedNotes.forEach { (fav, notes) ->
            item(
                span = {
                    GridItemSpan(columns) // Span across all columns
                }
            ) {
                if (fav == true) {
                    NotesHeader(text = "Favourites") {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .rotate(35f)
                                .size(32.dp)
                        )
                    }
                } else {
                    NotesHeader(text = "Notes") {
                        Icon(
                            painter = painterResource(id = R.drawable.note),
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            items(notes, key = { it.id }) { item ->
                GridListItem(
                    item,
                    navController,
                    selectedNotes,
                    folderMap,
                    editorViewModel,
                    notesViewModel,
                    onSnackbarUpdate,
                    is24HourClock
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridListItem(
    item: Note,
    navController: NavHostController,
    selectedNotes: MutableState<List<Note>>,
    folderMap: Map<String, Folder>,
    editorViewModel: NoteEditorViewModel,
    notesViewModel: NoteViewModel,
    onSnackbarUpdate: (String) -> Unit,
    is24HourClock: Boolean,
) {

    val options = listOf(
        MoreOption(
            id = 1,
            name = "Select",
            icon = Icons.Default.CheckCircleOutline,
            onClick = {
                selectedNotes.value += item
            }
        ),
        MoreOption(
            id = 2,
            name = if(item.isFavourite) "Unpin" else "Pin",
            icon = if(item.isFavourite) Icons.Filled.PushPin else Icons.Outlined.PushPin,
            onClick = {
                if(item.isFavourite) {
                    notesViewModel.unpinNote(item.id)
                    onSnackbarUpdate("Note unpinned")
                } else {
                    notesViewModel.pinNote(item.id)
                    onSnackbarUpdate("Note pinned")
                }
            }
        ),
        MoreOption(
            id = 3,
            name = "Move",
            icon = Icons.Default.DragIndicator,
            onClick = {
                selectedNotes.value += item
                editorViewModel.isOpenFolderList = true
            }
        ),
//        MoreOption(
//            id = 4,
//            name = if(item.isLocked) "Unlock" else "Lock",
//            icon = if(item.isLocked) Icons.Filled.Lock else Icons.Outlined.LockOpen,
//            onClick = {
//                if(item.isLocked) {
//                    notesViewModel.unlockNote(item.id)
//                    onSnackbarUpdate("Note unlocked")
//                } else {
//                    notesViewModel.lockNote(item.id)
//                    onSnackbarUpdate("Note locked")
//                }
//            }
//        ),
        MoreOption(
            id = 6,
            name = "Delete",
            icon = Icons.Default.Delete,
            onClick = {
                selectedNotes.value += item
                editorViewModel.isDeletePopupOpen = true
            }
        )
    )
    var isMoreOptionsOpen by remember { mutableStateOf(false) }


    val folderColor = folderMap[item.folderId]?.color
        ?.toColorInt()
        ?.let { Color(it) }
        ?: Primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(
                width = if (selectedNotes.value.contains(item)) 2.dp else 0.dp,
                color = if(selectedNotes.value.contains(item)) Primary else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall
            )
            .combinedClickable(
                onClick = {
                    if (selectedNotes.value.isEmpty()) {
                        item.id.let {
                            if(item.type == Type.CHECKLIST){
                                navController.navigate(
                                    Screen.EditChecklistScreen(it)
                                )
                            } else {
                                navController.navigate(
                                    Screen.EditNoteScreen(it)
                                )
                            }
                        }
                    } else {
                        if(selectedNotes.value.contains(item)) {
                            selectedNotes.value -= item
                        } else {
                            selectedNotes.value += item
                        }
                    }
                },
                onLongClick = {
                    selectedNotes.value += item
                },
            ),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(Color.White)
                        .background(folderColor)
                )
                Column (
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(Color.White)
                        .background(folderColor.copy(0.1f))
                        .padding(vertical = 12.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box (
                                modifier = Modifier
                                    .size(25.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.type == Type.CHECKLIST) {
                                    Image(
                                        painter = painterResource(R.drawable.list),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(R.drawable.note),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(28.dp),
                                    )
                                }
                            }
                            Text(
                                text = if (item.title.isEmpty()) "Empty Title" else item.title.replaceFirstChar { it.uppercase() },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                        if(selectedNotes.value.isEmpty()){
                            Column {
                                Box (
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable(
                                        ){
                                            isMoreOptionsOpen = true
                                        }
                                        .padding(horizontal = 5.dp)
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
                            .padding(horizontal = 10.dp)
                    ) {
                        if(item.type == Type.NOTE) {
                            Text(
                                text = if (item.body.isEmpty()) {
                                    "Empty Body"
                                } else {
                                    // Clean the HTML tags and get the plain text
                                    val plainText =
                                        Html.fromHtml(item.body, Html.FROM_HTML_MODE_LEGACY).toString()
                                            .trim()
                                    // Limit to 100 characters
                                    plainText.take(100).takeIf { it.isNotEmpty() } ?: "Empty Body"
                                },
                                fontSize = 12.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        } else {
                            val stringFromDb = item.body
                            val checklistItems = Json.decodeFromString<List<ChecklistItem>>(stringFromDb)

                            Column (
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
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
                                                modifier = Modifier.size(12.dp),
                                                tint = if (checkListItem.isCompleted) Primary else Color.Gray
                                            )
                                            Text(
                                                text = checkListItem.title,
                                                fontSize = 12.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                textDecoration = if (checkListItem.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                                color = if (checkListItem.isCompleted) Color.Gray else Color.Black
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier
                .padding(top = 70.dp)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, folderColor.copy(0.6f),
                            folderColor
                        ),
                        startY = 10f
                    )
                )
            )
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 5.dp, start = 5.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(11.dp)
                        )
                        Text(
                            text = item.updatedAt.toDashDateTime(),
                            fontSize = 10.sp,
                            color = Color.White,
                        )
                    }
                    if(item.reminderType == 2) {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(11.dp)
                            )
                            Text(
                                text = convertTo12HourFormat(item.reminderTime, is24HourClock),
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            if(selectedNotes.value.isNotEmpty()){
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    contentAlignment = Alignment.CenterEnd
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
                        if(selectedNotes.value.contains(item)) {
                            Spacer(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                        }
                    }
                }
            }
        }
    }
}