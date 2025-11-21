package com.edureminder.notebook.presentation.screen.folder_screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.codergalib2005.reorderable.ReorderableItem
import com.codergalib2005.reorderable.ReorderableLazyListState
import com.codergalib2005.reorderable.detectReorder
import com.codergalib2005.reorderable.detectReorderAfterLongPress
import com.codergalib2005.reorderable.rememberReorderableLazyListState
import com.codergalib2005.reorderable.reorderable
import com.edureminder.notebook.datastore.SettingsStore
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.presentation.screen.folder_screen.components.AddOrRenameFolderPopupOpen
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.folder.FolderViewModel
import com.edureminder.notebook.ui.NotepadTheme
import com.edureminder.notebook.ui.Pink40
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.ui.mode.ModeViewModel
import com.edureminder.notebook.ui.theme.ThemeViewModel
import com.edureminder.notebook.viewmodels.MainViewModel
import kotlinx.coroutines.launch




@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun FolderScreen(
    navController: NavHostController,
    folderViewModel: FolderViewModel = hiltViewModel(),
    noteEditorViewModel: NoteEditorViewModel = hiltViewModel()
) {
    val mainViewModel: MainViewModel = viewModel()
    val isPro by mainViewModel.isPro.collectAsState()

    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()

    val state = rememberReorderableLazyListState(onMove = { from, to ->
        val mutableFolders = folders.toMutableList()
        val item = mutableFolders.removeAt(from.index)

        // Shift positions
        if (from.index < to.index) {
            for (i in from.index until to.index) {
                mutableFolders[i].position = i + 1
            }
        } else {
            for (i in to.index until from.index) {
                mutableFolders[i].position = i + 2
            }
        }

        item.position = to.index + 1
        mutableFolders.add(to.index, item)

        // Save to DB
        coroutineScope.launch {
            folderViewModel.updateFolderPositions(mutableFolders)
        }
    })


    val context = LocalContext.current
    val modeViewModel = ModeViewModel(context)
    val themeViewModel = ThemeViewModel(context)
    val isDarkMode by modeViewModel.isDarkTheme.collectAsState()
    val isTheme by themeViewModel.isTheme.collectAsState()
    val settingStore = SettingsStore(context)
    val isSystemFont by settingStore.getUseSystemFontKey.collectAsState(initial = false)


    NotepadTheme(
        isTheme = isTheme,
        dynamicColor = true,
        useSystemFont = isSystemFont,
        isDarkMode = isDarkMode,
    ) {
        Scaffold(
            containerColor = Primary,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        if(!isPro && folders.size >= 5) {
                            navController.navigate(Screen.Subscription)
                        } else {
                            noteEditorViewModel.isAddOrRenameFolderPopupOpen = true
                            noteEditorViewModel.folderBeingEdited = null
                            noteEditorViewModel.folderName = ""
                        }
                    },
                    expanded = state.listState.isScrollingUp(),
                    icon = {
                        Icon(
                            Icons.Filled.Add,
                            "Localized Description",
                            tint = Color.White,
                            modifier = Modifier
                                .size(33.dp)
                        )
                    },
                    text = {
                        Text(
                            text = " New Folder",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = CircleShape
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(0.2f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Folder",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White)
                ) {
                    if(!isPro){
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .height(45.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(Pink40.copy(0.2f))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = Pink40
                                )
                                Text(
                                    text = "Free limit Folder: ${folders.size}/5",
                                    fontSize = 13.sp,
                                    color = Pink40
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Pink40)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Get unlimited",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    LazyColumn(
                        state = state.listState,
                        modifier = Modifier
                            .weight(1f)
                            .reorderable(state)
                            .detectReorderAfterLongPress(state),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(folders.sortedBy { it.position }, key = { it.id }) { folder ->
                            Column {
                                ReorderableItem(state, key = folder.id) { isDragging ->
                                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)
                                    FolderItem(
                                        folder = folder,
                                        reorderState = state,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(elevation)
                                            .background(Color.White)
                                            .padding(horizontal = 10.dp),
                                        onOpenForEditFolder = {
                                            noteEditorViewModel.folderBeingEdited = folder
                                            noteEditorViewModel.folderName = folder.name
                                            noteEditorViewModel.isAddOrRenameFolderPopupOpen = true
                                            noteEditorViewModel.selectedColor = folder.color
                                        },
                                        onDeleteFolder = {
                                            noteEditorViewModel.folderToDelete = folder
                                        }
                                    )
                                }
                                Spacer(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(
                                            if (folder.position == folders.size) 80.dp else 0.dp
                                        )
                                )
                            }
                        }
                    }
                }
            }

            if (noteEditorViewModel.isAddOrRenameFolderPopupOpen) {
                AddOrRenameFolderPopupOpen(
                    noteEditorViewModel,
                    folderViewModel,
                    mainViewModel,
                    navController
                )
            }


            if (noteEditorViewModel.folderToDelete != null) {
                Dialog(
                    onDismissRequest = {
                        noteEditorViewModel.folderToDelete = null
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                                Text(
                                    text = "Delete Folder?",
                                    fontSize = 18.sp,
                                    color = Primary
                                )
                            }
                            IconButton(
                                onClick = {
                                    noteEditorViewModel.folderToDelete = null
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

                        Text(
                            buildAnnotatedString {
                                append("Are you sure you want to delete ")
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Primary,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append("Folder ")
                                }
                                append("\"${noteEditorViewModel.folderToDelete?.name}\"? This action cannot be ")
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Primary,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append("undone")
                                }
                            },
                            color = Color.Black.copy(0.7f),
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    noteEditorViewModel.folderToDelete = null
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
//                                if(isDeletePopupOpen != null) folderViewModel.deleteFolder(folderBeingEdited!!)
                                    noteEditorViewModel.folderToDelete?.let {
                                        folderViewModel.deleteFolder(it)
                                    }
                                    noteEditorViewModel.folderToDelete = null

                                },
                                modifier = Modifier
                                    .width(150.dp),
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
@Composable
fun FolderItem(
    folder: Folder,
    reorderState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
    onOpenForEditFolder: () -> Unit,
    onDeleteFolder: () -> Unit
) {
    var isMoreOptionsOpen by remember { mutableStateOf(false) }
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            /**
             * This box will be used to drag and drop the folder
             */
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(0.1f))
                    .detectReorder(reorderState),

                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = Primary
                )
            }
            Text(
                text = folder.name + "(${folder.count})",
                color = Color.Black.copy(0.7f),
                fontSize = 16.sp
            )
        }
        Column {
            IconButton(
                onClick = {
                    isMoreOptionsOpen = !isMoreOptionsOpen
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.Black.copy(0.5f)
                )
            }

            DropdownMenu(
                expanded = isMoreOptionsOpen,
                onDismissRequest = {
                    isMoreOptionsOpen = false
                },
                modifier = Modifier
                    .widthIn(min = 150.dp),
                containerColor = Color.White,
                shape = MaterialTheme.shapes.extraSmall,
            ) {
                DropdownMenuItem(
                    onClick = {
                        onOpenForEditFolder()
                        isMoreOptionsOpen = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .semantics {
                            contentDescription = "Rename"
                        },
                    text = {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.Default.ModeEdit,
                                contentDescription = null,
                                tint = Color.Black.copy(0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Rename",
                                fontSize = 15.sp,
                                color = Color.Black.copy(0.7f),
                            )
                        }
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDeleteFolder()
                        isMoreOptionsOpen = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .semantics {
                            contentDescription = "Delete"
                        },
                    text = {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Black.copy(0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Delete",
                                fontSize = 15.sp,
                                color = Color.Black.copy(0.7f),
                            )
                        }
                    },
                )
            }
        }
    }
}