package com.edureminder.notebook.presentation.screen.checklist_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.screen.edit_note.ChecklistItem
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.presentation.screen.edit_note.components.FolderList
import com.edureminder.notebook.presentation.screen.edit_note.components.MoreOption
import com.edureminder.notebook.presentation.screen.edit_note.components.MoreOptions
import com.edureminder.notebook.presentation.screen.edit_note.components.ReminderDialog
import com.edureminder.notebook.presentation.screen.edit_note.components.generateChecklistPDF
import com.edureminder.notebook.presentation.screen.folder_screen.components.AddOrRenameFolderPopupOpen
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.folder.FolderViewModel
import com.edureminder.notebook.room.note.NoteViewModel
import com.edureminder.notebook.ui.ColorBlack
import com.edureminder.notebook.ui.ColorWhite
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import java.io.File


@Composable
fun AddChecklistContent(
    navController: NavController,
    screenFrom: String,
    onPDFGenerate: File,
    editorViewModel: NoteEditorViewModel,
    folders: List<Folder>,
    notesViewModel: NoteViewModel,
    folderViewModel: FolderViewModel,
    onSaveNote: () -> Unit,
    onSnackbarUpdate: (String) -> Unit,
) {
    val mainViewModel: MainViewModel = viewModel()
    val isPro by mainViewModel.isPro.collectAsState()

    val MAX_FREE_ITEMS = 15
    val MAX_PRO_ITEMS = 100

    val currentMax = if (isPro) MAX_PRO_ITEMS else MAX_FREE_ITEMS

    var limitMessage by remember { mutableStateOf("") }

    val theme = editorViewModel.theme
    val editorFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val moreOptions = buildList {
        add(
            MoreOption(
                icon = if (editorViewModel.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                name = "Pin",
                onClick = {
                    editorViewModel.isPinned = !editorViewModel.isPinned
                }
            )
        )
        add(
            MoreOption(
                icon = if (editorViewModel.isLocked) Icons.Filled.Lock else Icons.Outlined.LockOpen,
                name = "Lock",
                onClick = {
                    editorViewModel.isLocked = !editorViewModel.isLocked
                }
            )
        )
//        add(
//            MoreOption(
//                icon = Icons.Default.Share,
//                name = "Share",
//                onClick = {
//                    // Handle Note Share option click
//                }
//            )
//        )
        add(
            MoreOption(
                icon = Icons.Default.NotificationsActive,
                name = "Reminder",
                onClick = {
                    editorViewModel.showReminderDialog = true
                }
            )
        )
        add(
            MoreOption(
                icon = Icons.Default.Theaters,
                name = "Theme",
                onClick = {
                    editorViewModel.showThemeSheet = true
                }
            )
        )
        if (screenFrom == "edit_note") {
            add(
                MoreOption(
                    icon = Icons.Default.Delete,
                    name = "Delete",
                    onClick = {
                        editorViewModel.isDeletePopupOpen = true
                    }
                )
            )
        }
    }
    val MAX_CHAR = 200

    var itemToFocus by remember { mutableStateOf<Int?>(null) }

    // Initialize focus requesters
    val checklistFocusRequesters = remember {
        mutableStateListOf<FocusRequester>().apply {
            addAll(editorViewModel.checklist.map { FocusRequester() })
        }
    }

    // Sync focus requesters with checklist
    LaunchedEffect(editorViewModel.checklist.size) {
        while (checklistFocusRequesters.size < editorViewModel.checklist.size) {
            checklistFocusRequesters.add(FocusRequester())
        }
        while (checklistFocusRequesters.size > editorViewModel.checklist.size) {
            checklistFocusRequesters.removeAt(checklistFocusRequesters.lastIndex)
        }
    }

    LaunchedEffect(editorViewModel.checklist) {
        if ((editorViewModel.checklist.isEmpty() || editorViewModel.checklist.last().title.isNotBlank())) {
            if (editorViewModel.checklist.size < currentMax) {
                // Add new item
                val newItem = ChecklistItem(
                    id = editorViewModel.checklist.size + 1,
                    title = "",
                    isCompleted = false
                )
                editorViewModel.checklist = editorViewModel.checklist + newItem
                checklistFocusRequesters.add(FocusRequester())
                limitMessage = "" // clear any previous message
            } else {
                // Reached limit
                limitMessage = if (isPro) {
                    "You’ve reached the maximum."
                } else {
                    "Try Pro to add more items!"
                }
            }
        }
    }


    // Handler function to enforce the 100-character limit
    fun handleTextChange(input: String) {
        if (input.length <= 100) {
            editorViewModel.title = input
        }
    }

    val backgroundColor = Color(editorViewModel.theme.backgroundColor.toColorInt())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .background(Primary.copy(0.1f))
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .background(Primary)
                .padding(horizontal = 5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isPro) {
                                generateChecklistPDF(
                                    context,
                                    onPDFGenerate,
                                    editorViewModel.checklist,
                                    editorViewModel.title
                                )
                            } else {
                                onSnackbarUpdate("Upgrade to Pro to download or share your note!")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                    if(editorViewModel.isEditable) Box (
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                onSaveNote()
                                editorViewModel.isEditable = false
                            }
                            .padding(horizontal = 14.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Save",
                            color = Primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp,
                        )
                    } else {
                        IconButton(
                            onClick = {
                                editorViewModel.isEditable = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                    }
                    Column {
                        IconButton(
                            onClick = {
                                editorViewModel.isMoreOptionsOpen = !editorViewModel.isMoreOptionsOpen
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                        MoreOptions(
                            moreOptions,
                            editorViewModel
                        )
                    }
//                    IconButton(onClick = { isPinned.value = !isPinned.value }) {
//                        Icon(
//                            imageVector = if (isPinned.value) Icons.Filled.PushPin else Icons.Outlined.PushPin,
//                            contentDescription = null,
//                            tint = Color.Black,
//                            modifier = Modifier
//                                .rotate(35f)
//                                .size(26.dp)
//                        )
//                    }
//                    Column {
//                        IconButton(onClick = {
//                            isMoreSettingsOpen.value = true
//                        }) {
//                            Icon(
//                                imageVector = Icons.Default.MoreVert,
//                                contentDescription = null,
//                                tint = Color.Black,
//                                modifier = Modifier.size(26.dp)
//                            )
//                        }
//                        MoreSettings(
//                            isOpenMoreSettings = isMoreSettingsOpen,
//                            onDismissRequest = {
//                                isMoreSettingsOpen.value = false
//                            }
//                        )
//                    }
                }
            }
        }

        Column (
            modifier = Modifier
                .background(Color.White)
//                .background(Color(0xFFFFE0E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Edited: ${editorViewModel.getCurrentDate()}",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 10.dp)
                )
                editorViewModel.selectedFolder?.let {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ColorWhite)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                editorViewModel.isOpenFolderList = !editorViewModel.isOpenFolderList
                            }
                            .padding(start = 9.dp, end = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            color = ColorBlack.copy(0.8f),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .padding(start = 5.dp)
                        )
                        Icon(
                            imageVector = if(editorViewModel.isOpenFolderList) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            tint = Color.Gray,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        if(editorViewModel.isOpenFolderList) FolderList(
                            onTagSelected = { selectedItem ->
                                editorViewModel.selectedFolder = selectedItem
                                editorViewModel.isOpenFolderList = false
                            },
                            folders,
                            editorViewModel,
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .background(backgroundColor)
        ) {
//            MarkdownEditorContent(
//                richTextState,
//                editorFocusRequester,
//                theme,
//                editorViewModel
//            )
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(Color.Transparent)
                        .focusRequester(editorFocusRequester),
                    value = editorViewModel.title,
                    onValueChange = {
                        handleTextChange(it)
                    },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(0.9f)
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .padding(horizontal = 0.dp, vertical = 10.dp)
                                .fillMaxWidth()
                        ) {
                            if (editorViewModel.title.isEmpty()) {
                                Text(
                                    text = "Edit Title...",
                                    color = Color.Gray,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                            innerTextField()
                        }
                    },
                    maxLines = 1,
                    singleLine = true,
                    enabled = editorViewModel.isEditable
                )
                Column  (
                    modifier = Modifier
                        .imePadding()
                        .navigationBarsPadding()
                ){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .combinedClickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {},
                                onDoubleClick = {
                                    if(!editorViewModel.isEditable){
                                        editorViewModel.isEditable = true
                                    }
                                }
                            )
                    ) {
                        itemsIndexed(editorViewModel.checklist) { index, item ->
                            val currentFocusRequester = remember { FocusRequester() }

                            LaunchedEffect(Unit) {
                                if (checklistFocusRequesters.size <= index) {
                                    checklistFocusRequesters.add(currentFocusRequester)
                                } else {
                                    checklistFocusRequesters[index] = currentFocusRequester
                                }
                            }
                            CheckListItem(
                                item = item,
                                index = index,
                                onEditCheckItem = { text ->
                                    if (text.length <= MAX_CHAR) {
                                        editorViewModel.checklist =
                                            editorViewModel.checklist.toMutableList().also {
                                                it[index] = it[index].copy(title = text)
                                            }
                                    } else {
                                        editorViewModel.checklist =
                                            editorViewModel.checklist.toMutableList().also {
                                                it[index] = it[index].copy(title = text.take(MAX_CHAR))
                                            }
                                    }
                                },
                                onDeleteCheckItem = {
                                    editorViewModel.checklist =
                                        editorViewModel.checklist.toMutableList().also {
                                            it.removeAt(index)
                                        }
                                    limitMessage = "" // reset message after deleting
                                },
                                onDoneCheckItem = {
                                    if (index == editorViewModel.checklist.size - 1) {
                                        focusManager.clearFocus()
                                    } else if (index + 1 < editorViewModel.checklist.size) {
                                        itemToFocus = editorViewModel.checklist[index + 1].id
                                    }
                                },
                                onCompletedCheckItem = {
                                    val updatedItem = item.copy(isCompleted = !item.isCompleted)
                                    editorViewModel.checklist =
                                        editorViewModel.checklist.toMutableList().also {
                                            it[index] = updatedItem
                                        }
                                    if (updatedItem.isCompleted && index + 1 < editorViewModel.checklist.size) {
                                        itemToFocus = editorViewModel.checklist[index + 1].id
                                    }
                                },
                                focusRequester = checklistFocusRequesters.getOrNull(index),
                                shouldRequestFocus = item.id == itemToFocus,
                                isEditable = editorViewModel.isEditable,
                                isLast = index == editorViewModel.checklist.size - 1
                            )
                        }
                    }
                    Column (
                        modifier = Modifier
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = limitMessage.isNotEmpty(),
                            modifier = Modifier
                                .zIndex(2f)
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (!isPro) {
                                        Text(
                                            text = limitMessage,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    navController.navigate(Screen.Subscription){
                                                        popUpTo(Screen.MainScreen) {
                                                            inclusive = false
                                                        }
                                                    }
                                                }
                                                .padding(horizontal = 18.dp, vertical = 7.dp),
                                        ) {
                                            Text(
                                                text = "Pro",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = limitMessage,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    color = Primary.copy(0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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

    if(editorViewModel.showReminderDialog){
        ReminderDialog(
            editorViewModel,
            mainViewModel,
            onDismissRequest = {
                editorViewModel.showReminderDialog = false
            }
        )
    }
}

@Composable
fun CheckListItem(
    item: ChecklistItem,
    index: Int,
    onEditCheckItem: (String) -> Unit,
    onDeleteCheckItem: () -> Unit,
    onDoneCheckItem: () -> Unit,
    onCompletedCheckItem: () -> Unit,
    focusRequester: FocusRequester? = null,
    shouldRequestFocus: Boolean = false,
    isEditable: Boolean = true,
    isLast: Boolean = false
) {
    var hasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(shouldRequestFocus) {
        if (shouldRequestFocus && !hasFocus) {
            delay(100) // Small delay to ensure composition is complete
            try {
                focusRequester?.requestFocus()
            } catch (e: IllegalStateException) {
                // Log or handle error
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        verticalAlignment = Alignment.Top
    ) {
        if (item.title.isBlank()) {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 15.dp, start = 10.dp)
                    .size(25.dp)
                    .clip(CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusRequester?.requestFocus()
                    },
                tint = Primary
            )
        } else {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onCompletedCheckItem() },
                enabled = isEditable,
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.White
                )

            )
        }

        TextField(
            value = item.title,
            onValueChange = {
                onEditCheckItem(it)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    text = "What's on your mind?",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black.copy(0.5f)
                    )
                )
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = if(item.isCompleted) Color.Gray else Color.Black.copy(0.9f),
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            modifier =
                if (focusRequester != null)
                    Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                else
                    Modifier
                        .weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onDone = { onDoneCheckItem() }
            ),
            enabled = isEditable
        )

        if (isEditable && !item.title.isBlank()) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(top = 15.dp)
                    .clickable (
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {

                        onDeleteCheckItem()
                    },
                tint = Color.Gray
            )
        }
    }
}