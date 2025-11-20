package com.edureminder.easynotes.presentation.screen.main_screen.note_views

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.preferences.notes.NotesSettingsViewModel
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.room.note.Status
import com.edureminder.easynotes.ui.Container
import com.edureminder.easynotes.viewmodels.MainViewModel
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import com.edureminder.easynotes.preferences.notes.SortOrder
import com.edureminder.easynotes.preferences.notes.ViewType
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.components.NoteDeletePopup
import com.edureminder.easynotes.presentation.screen.folder_screen.components.AddOrRenameFolderPopupOpen
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.note.Type
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteView(
    navController: NavHostController,
    onSnackbarUpdate : (String) -> Unit
) {
    var isSearchOpen by remember { mutableStateOf(false) }
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    val searchText = rememberSaveable {
        mutableStateOf("")
    }
    val listState = rememberLazyListState()

    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 }
    }

    val context = LocalContext.current
    val settingsStore = SettingsStore(context)
    val is24HourClock by settingsStore.getClockKey.collectAsState(initial = false)
    val mainViewModel: MainViewModel = viewModel()
    val isPro by mainViewModel.isPro.collectAsState()
    val settingsViewModel: NotesSettingsViewModel = hiltViewModel()
    val viewType by settingsViewModel.viewType.collectAsState()
    val sortOrder = settingsViewModel.sortOrder.collectAsState()
    val noteType = settingsViewModel.noteType.collectAsState()

    val notesViewModel: NoteViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = hiltViewModel()
    var selectedFolderId by rememberSaveable { mutableStateOf("0") }
    var isLockedNote by rememberSaveable { mutableStateOf(false) }

    val notesListFlow: State<List<Note>> = notesViewModel
        .getNotesByStatusAndSort(
            status = Status.ACTIVE,
            sortOrder = sortOrder.value, // Make sure sortOrder.value is of type SortOrder
            selectedFolderId = selectedFolderId,
            filterByLock = true,
            locked = isLockedNote,
            noteType.value
        )
        .collectAsState(initial = emptyList())

    val pendingCount by notesViewModel.getPendingSyncNoteCount().collectAsState(initial = 0)

    val currentSearchText = rememberUpdatedState(searchText.value)
    val searchBarVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isMoreFeatureOpened = remember {
        mutableStateOf(false)
    }

    var isSettingOpened by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val folderMap = folders.associateBy { it.id }
    val selectedNotes = remember {
        mutableStateOf(emptyList<Note>())
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (selectedNotes.value.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .drawBehind {
                        if (isScrolled) {
                            val shadowHeight = 12.dp.toPx() // adjust shadow thickness
                            drawIntoCanvas { canvas ->
                                val paint = Paint().asFrameworkPaint()
                                paint.color = android.graphics.Color.BLACK
                                paint.alpha = (0.2f * 255).toInt()
                                paint.maskFilter =
                                    BlurMaskFilter(shadowHeight, BlurMaskFilter.Blur.NORMAL)
                                canvas.nativeCanvas.drawRect(
                                    0f,
                                    size.height - shadowHeight,
                                    size.width,
                                    size.height,
                                    paint
                                )
                            }
                        }
                    }
                    .background(Container),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
//                         onToggleSidebar()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .size(28.dp)
                        )
                    }
                    if (!searchBarVisible.value) {
                        Text(
                            text = "Diary",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(1f)

                ) {
                    if (isSearchOpen) {
                        BasicTextField(
                            value = searchText.value,
                            onValueChange = {
                                if (it.length <= 30) {
                                    searchText.value = it
                                }
                            },
                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(0.4f)),
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 13.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.search),
                                            contentDescription = "search icon",
                                            tint = Color.Gray,
                                            modifier = Modifier
                                                .size(20.dp)
                                        )
                                        if (searchText.value.isEmpty()) {
                                            Text(
                                                text = "Search...",
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        } else {
                                            innerTextField()
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "close icon",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                            ) {
                                                if (searchText.value.isNotEmpty()) {
                                                    searchText.value = ""
                                                } else {
                                                    isSearchOpen = false
                                                }
                                            }
                                    )

                                }
                            }
                        )
                    } else {
//                    Row (
//                        modifier = Modifier
//                            .padding(end = 10.dp)
//                            .clip(MaterialTheme.shapes.extraSmall)
//                            .background(Color.Red)
//                            .padding(horizontal = 5.dp, vertical = 3.dp),
//                        horizontalArrangement = Arrangement.spacedBy(3.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.crown_icon),
//                            contentDescription = "crown icon",
//                            tint = Color.White,
//                            modifier = Modifier
//                                .size(15.dp)
//                        )
//                        Text(
//                            text = "PREMIUM",
//                            fontSize = 10.sp,
//                            color = Color.White,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
                        IconButton(
                            onClick = {
                                isSearchOpen = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "search icon",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                isSettingOpened = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.option),
                                contentDescription = "search icon",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                        Column {
                            DropdownMenu(
                                expanded = isMoreFeatureOpened.value,
                                onDismissRequest = { isMoreFeatureOpened.value = false },
                                modifier = Modifier
                                    .width(170.dp),
                                containerColor = ColorWhite,
                                shadowElevation = 2.dp,
                            ) {
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        isMoreFeatureOpened.value = false
                                        navController.navigate(Screen.AuthScreen)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            contentDescription = "Locked"
                                        },
                                    text = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked icon",
                                                tint = ColorBlack.copy(0.8f),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = "Locked",
                                                color = ColorBlack.copy(0.8f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                )
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        navController.navigate(Screen.RecycleNoteScreen)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            contentDescription = "Recycle bin"
                                        },
                                    text = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Recycling,
                                                contentDescription = "Recycle bin icon",
                                                tint = ColorBlack.copy(0.8f),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = "Recycle bin",
                                                color = ColorBlack.copy(0.8f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                )
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        navController.navigate(Screen.SettingScreen)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            contentDescription = "Setting"
                                        },
                                    text = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Setting icon",
                                                tint = ColorBlack.copy(0.8f),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = "Setting",
                                                color = ColorBlack.copy(0.8f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                )
                            }
                        }
                        IconButton(onClick = {
                            isMoreFeatureOpened.value = !isMoreFeatureOpened.value

                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Primary)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        selectedNotes.value = emptyList()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "${selectedNotes.value.size} - Selected",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedNotes.value.size == 1) {

                        IconButton(
                            onClick = {
                                val selectedNote = selectedNotes.value.first()
                                if (selectedNote.isFavourite) {
                                    notesViewModel.unpinNote(selectedNote.id)
                                } else {
                                    notesViewModel.pinNote(selectedNote.id)
                                }
                                selectedNotes.value = emptyList()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .rotate(35f)
                            )
                        }
                    }
                }
            }
        }

        Column {
            AnimatedVisibility(visible = selectedNotes.value.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = CircleShape,
                                    )
                                    .background(if (selectedFolderId == "0") Primary else ColorWhite)
                                    .clickable(
                                        enabled = selectedFolderId != "0"
                                    ) {
                                        selectedFolderId = "0"
                                    }
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 5.dp
                                    )
                            ) {
                                Text(
                                    text = "All",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFolderId == "0") Color.White else ColorBlack.copy(
                                        0.6f
                                    )
                                )
                            }
                        }
                        items(folders) { folder ->
                            FolderItem(
                                folder,
                                selectedFolderId,
                                onChange = {
                                    selectedFolderId = folder.id
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.padding(start = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = CircleShape,
                                )
                                .background(Color.White)
                                .clickable() {
                                    navController.navigate(Screen.FolderScreen)
                                }
                                .padding(
                                    horizontal = 15.dp,
                                    vertical = 5.dp
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = ColorBlack.copy(0.7f),
                                modifier = Modifier
                                    .size(19.dp)
                            )
                        }
                    }
                }
            }
            if (notesListFlow.value.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(50.dp)
                            .alpha(0.8f),
                        painter = painterResource(id = R.drawable.note),
                        contentDescription = null,
                        tint = Primary
                    )
                    Text(
                        text = "No Notes / Checklist",
                        fontSize = 30.sp,
                        color = Primary,
                        fontWeight = FontWeight.Light
                    )
                }
            } else {
                NotesList(
                    notesViewModel,
                    navController,
                    state = listState,
                    notesListFlow.value,
                    currentSearchText,
                    viewType,
                    selectedNotes,
                    folderMap,
                    editorViewModel,
                    onSnackbarUpdate,
                    is24HourClock
                )
            }

        }

        if (editorViewModel.isOpenFolderList) ChangeFolderOfNote(
            folders,
            editorViewModel,
            selectedNotes,
            notesViewModel,
            onSnackbarUpdate
        )

        if (editorViewModel.isAddOrRenameFolderPopupOpen) {
            AddOrRenameFolderPopupOpen(
                editorViewModel,
                folderViewModel,
                mainViewModel,
                navController,
                onDismissRequest = {
                    editorViewModel.isOpenFolderList = true
                }
            )
        }

        if (isSettingOpened) {
            ModalBottomSheet(
                onDismissRequest = { isSettingOpened = false },
                sheetState = sheetState,
                dragHandle = null,
                containerColor = ColorWhite,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(top = 15.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        HorizontalDivider(color = Color.LightGray)
                        Text(
                            text = "Filter Type",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                                .background(Color.White).padding(horizontal = 10.dp)
                        )
                    }
                    SettingItem(
                        icon = Icons.Default.FilterList,
                        label = "All Notes & Checklists",
                        selected = noteType.value == null,
                        enabled = noteType.value != null
                    ) {
                        settingsViewModel.setNoteType(null)
                    }

                    SettingItem(
                        icon = Icons.Default.Checklist,
                        label = "Checklist Only",
                        selected = noteType.value == Type.CHECKLIST,
                        enabled = noteType.value != Type.CHECKLIST
                    ) {
                        settingsViewModel.setNoteType(Type.CHECKLIST)
                    }
                    SettingItem(
                        icon = Icons.AutoMirrored.Filled.Notes,
                        label = "Notes Only",
                        selected = noteType.value == Type.NOTE,
                        enabled = noteType.value != Type.NOTE
                    ) {
                        settingsViewModel.setNoteType(Type.NOTE)
                    }

                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        HorizontalDivider(color = Color.LightGray)
                        Text(
                            text = "View Type",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                                .background(Color.White).padding(horizontal = 10.dp)
                        )
                    }
                    // View Types
                    SettingItem(
                        icon = Icons.AutoMirrored.Filled.List,
                        label = "List view",
                        selected = viewType == ViewType.LIST,
                        enabled = viewType != ViewType.LIST
                    ) {
                        settingsViewModel.setViewType(ViewType.LIST)
                    }

                    SettingItem(
                        icon = Icons.Default.GridView,
                        label = "Grid view",
                        selected = viewType == ViewType.GRID,
                        enabled = viewType != ViewType.GRID
                    ) {
                        settingsViewModel.setViewType(ViewType.GRID)
                    }

                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        HorizontalDivider(color = Color.LightGray)
                        Text(
                            text = "Sort By",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                                .background(Color.White).padding(horizontal = 10.dp)
                        )
                    }
                    // Sort Options
                    SettingItem(
                        icon = Icons.Default.Title,
                        label = "A-Z",
                        selected = sortOrder.value == SortOrder.TITLE_ASCENDING,
                        enabled = sortOrder.value != SortOrder.TITLE_ASCENDING
                    ) {
                        settingsViewModel.setSortOrder(SortOrder.TITLE_ASCENDING)
                    }

                    SettingItem(
                        icon = Icons.Default.Title,
                        label = "Z-A",
                        selected = sortOrder.value == SortOrder.TITLE_DESCENDING,
                        enabled = sortOrder.value != SortOrder.TITLE_DESCENDING
                    ) {
                        settingsViewModel.setSortOrder(SortOrder.TITLE_DESCENDING)
                    }

                    SettingItem(
                        icon = Icons.Default.DateRange,
                        label = "NEWEST",
                        selected = sortOrder.value == SortOrder.DATE_NEWEST_FIRST,
                        enabled = sortOrder.value != SortOrder.DATE_NEWEST_FIRST
                    ) {
                        settingsViewModel.setSortOrder(SortOrder.DATE_NEWEST_FIRST)
                    }

                    SettingItem(
                        icon = Icons.Default.DateRange,
                        label = "OLDEST",
                        selected = sortOrder.value == SortOrder.DATE_OLDEST_FIRST,
                        enabled = sortOrder.value != SortOrder.DATE_OLDEST_FIRST
                    ) {
                        settingsViewModel.setSortOrder(SortOrder.DATE_OLDEST_FIRST)
                    }

                    // 🔽 New Sort Options (Updated At)
                    SettingItem(
                        icon = Icons.Default.History,
                        label = "Last updated",
                        selected = sortOrder.value == SortOrder.UPDATED_AT_NEWEST_FIRST,
                        enabled = sortOrder.value != SortOrder.UPDATED_AT_NEWEST_FIRST
                    ) {
                        settingsViewModel.setSortOrder(SortOrder.UPDATED_AT_NEWEST_FIRST)
                    }

                    SettingItem(
                        icon = Icons.Default.History,
                        label = "First updated",
                        selected = sortOrder.value == SortOrder.UPDATED_AT_OLDEST_FIRST,
                        enabled = sortOrder.value != SortOrder.UPDATED_AT_OLDEST_FIRST
                    ) {
                        settingsViewModel.setSortOrder(SortOrder.UPDATED_AT_OLDEST_FIRST)
                    }
                }
            }
        }

        if (editorViewModel.isDeletePopupOpen) {
            NoteDeletePopup(
                editorViewModel,
                notesViewModel,
                selectedNotes.value.map { it.id },
                onEmptySelectedNotes = {
                    selectedNotes.value = emptyList()
                    editorViewModel.isDeletePopupOpen = false
                },
                onSnackbarUpdate
            )
        }
    }
}
@Composable
fun ChangeFolderOfNote(
    folders: List<Folder>,
    editorViewModel: NoteEditorViewModel,
    selectedNotes: MutableState<List<Note>>,
    notesViewModel: NoteViewModel,
    onSnackbarUpdate: (String) -> Unit
) {
    var selectedFolderId by rememberSaveable { mutableStateOf<Folder?>(null) }

    LaunchedEffect(key1 = folders) {
        if (folders.isNotEmpty()) {
            selectedFolderId = folders.first()
        }
    }

    Dialog(
        onDismissRequest = {
            editorViewModel.isOpenFolderList = false
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            editorViewModel.isOpenFolderList = false
                            /**
                             *
                             */
                            editorViewModel.isAddOrRenameFolderPopupOpen = true
                            editorViewModel.folderBeingEdited = null
                            editorViewModel.folderName = ""
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Primary.copy(0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
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
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Primary.copy(0.1f)
                        )
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
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .selectableGroup(),
                ) {
                    items(folders) { folder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = folder.id != "none",
                                ) {
                                    selectedFolderId = folder
                                }
                                .padding(horizontal = 15.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = (folder.id == selectedFolderId?.id),
                                onClick = null // null recommended for accessibility with screen readers
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
                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height(45.dp)
                            .width(200.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedFolderId != null) Primary else Color.Gray
                            )
                            .clickable(
                                enabled = selectedFolderId != null,
                            ) {
                                selectedFolderId?.let {
                                    val notesToDelete = selectedNotes.value
                                    if (notesToDelete.isNotEmpty()) {
                                        val ids = notesToDelete.map { it.id }
                                        if (ids.isNotEmpty()) {
                                            notesViewModel.updateFolderIdForNotes(
                                                ids,
                                                it.id
                                            )
                                            onSnackbarUpdate("Moved to ${it.name}")
                                        }
                                    }
                                    selectedNotes.value = emptyList()
                                    editorViewModel.isOpenFolderList = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (selected) Primary else ColorBlack.copy(0.8f)
        )
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Primary else ColorBlack.copy(0.8f)
        )
    }
}
@Composable
fun FolderItem(
    folder: Folder,
    selectedFolderId: String,
    onChange: () -> Unit
) {
    Box (
        modifier = Modifier
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
            )
            .background(if(selectedFolderId == folder.id) Primary else ColorWhite)
            .clickable(
                enabled = selectedFolderId != folder.id
            ){
                onChange()
            }
            .padding(
                horizontal = 20.dp,
                vertical = 5.dp
            )
    ) {
        Text(
            text = folder.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if(selectedFolderId == folder.id) Color.White else ColorBlack.copy(0.6f)
        )
    }
}
