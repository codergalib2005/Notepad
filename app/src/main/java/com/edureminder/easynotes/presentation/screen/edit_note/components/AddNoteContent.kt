package com.edureminder.easynotes.presentation.screen.edit_note.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.lib.richeditor.markdowneditor.MarkdownEditorContent
import com.edureminder.easynotes.presentation.components.MaterialTimePicker
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.SelectedDay
import com.edureminder.easynotes.presentation.screen.folder_screen.components.AddOrRenameFolderPopupOpen
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.viewmodels.MainViewModel
import com.feature.edureminder.texteditor.model.RichTextState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


data class MoreOption(
    val icon: ImageVector,
    val name: String,
    val onClick: () -> Unit
)

@Composable
fun AddNotesContent(
    richTextState: RichTextState,
    navController: NavHostController,
    screenFrom: String,
    onPDFGenerate: File,
    editorViewModel: NoteEditorViewModel,
    folders: List<Folder>,
    folderViewModel: FolderViewModel,
    onSaveNote: () -> Unit,
    backgroundColor: Color,
    onSnackbarUpdate: (String) -> Unit,
) {
    val mainViewModel: MainViewModel = viewModel()
    val isPro by mainViewModel.isPro.collectAsState()

    val theme = editorViewModel.theme
    val titleFocusRequester = remember { FocusRequester() }
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
//        add(
//            MoreOption(
//                icon = if (editorViewModel.isLocked) Icons.Filled.Lock else Icons.Outlined.LockOpen,
//                name = "Lock",
//                onClick = {
//                    editorViewModel.isLocked = !editorViewModel.isLocked
//                }
//            )
//        )
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


    // Observe `isEditable` and control focus behavior
    LaunchedEffect(editorViewModel.isEditable) {
        if (editorViewModel.isEditable) {
            // Maintain focus on the editor for edit mode
            editorFocusRequester.requestFocus()
            keyboardController?.show()
        } else {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }
    // Focus the BasicTextField initially if screenFrom is 'add_note'
    LaunchedEffect(screenFrom) {
        if (screenFrom == "add_note") {
            titleFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Handler function to enforce the 100-character limit
    fun handleTextChange(input: String) {
        if (input.length <= 100) {
            editorViewModel.title = input
        }
    }

    val headerColor = Color(editorViewModel.theme.headerColor.toColorInt())

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .statusBarsPadding()
                .background(headerColor)
        ) {
            if(editorViewModel.theme.cat != "solid" && !editorViewModel.theme.showTopLine){
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = editorViewModel.theme.value),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
            Column(
                modifier = Modifier
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
                            onClick = {
                                if (isPro) {
                                    generatePDF(
                                        context,
                                        onPDFGenerate,
                                        richTextState.toHtml(),
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
                    }
                }
            }
        }

        Column (
            modifier = Modifier
                .background(backgroundColor)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 5.dp),
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(Color.Transparent)
                        .focusRequester(titleFocusRequester),
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
                                    text = "Title",
                                    color = Color.Gray,
                                    fontSize = 20.sp,
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
            }
            MarkdownEditorContent(
                richTextState,
                editorFocusRequester,
                theme,
                editorViewModel,
                backgroundColor,
                navController,
                mainViewModel
            )
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
fun MoreOptions(
    moreOptions: List<MoreOption>,
    editorViewModel: NoteEditorViewModel,
) {

    DropdownMenu(
        expanded = editorViewModel.isMoreOptionsOpen,
        onDismissRequest = {
            editorViewModel.isMoreOptionsOpen = !editorViewModel.isMoreOptionsOpen
        },
        modifier = Modifier
            .widthIn(min = 150.dp),
        containerColor = Color.White,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(0.dp),
        offset = DpOffset(x = 5.dp, y = (-15).dp)
    ) {
        moreOptions.forEach { item ->
            if(item.name.lowercase() == "delete") HorizontalDivider(
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            DropdownMenuItem(
                onClick = {
                    item.onClick()
                    editorViewModel.isMoreOptionsOpen = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .semantics {
                        contentDescription = item.name
                    },
                text = {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            modifier = Modifier
                                .rotate(if(item.name.lowercase() == "pin") 35f else 0f)
                                .size(20.dp),
                            tint = if(item.name.lowercase() == "delete") Color.Red else Color.Black.copy(0.6f)
                        )
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            color =  if(item.name.lowercase() == "delete") Color.Red else Color.Black.copy(0.7f),
                            fontWeight = FontWeight.W400
                        )
                    }
                },
            )
        }
    }
}

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


data class ReminderType(
    val id: Int,
    val name: String,
    val icon: ImageVector
)
val reminderTypes = listOf(
    ReminderType(
        id = 1,
        name = "Don't remind",
        icon = Icons.Default.NotificationsOff
    ),
    ReminderType(
        id = 2,
        name = "Notification",
        icon = Icons.Default.NotificationsActive
    )
)

@Composable
fun ReminderDialog(
    editorViewModel: NoteEditorViewModel,
    mainViewModel: MainViewModel,
    onDismissRequest: () -> Unit
){
    val context = LocalContext.current
    val settingsStore = SettingsStore(context)
    val is24HoursEnabled = settingsStore.getClockKey.collectAsState(initial = false).value!!
    val isTimeDialogShowing = remember { mutableStateOf(false) }

    var timeText by remember { mutableStateOf(editorViewModel.timeText) }
    var selectReminderType by remember { mutableStateOf(editorViewModel.selectReminderType) }

    var repeatableDays by remember {
        mutableStateOf(
            listOf(
                SelectedDay(
                    Calendar.SUNDAY
                ),
                SelectedDay(
                    Calendar.MONDAY
                ),
                SelectedDay(
                    Calendar.TUESDAY
                ),
                SelectedDay(
                    Calendar.WEDNESDAY
                ),
                SelectedDay(
                    Calendar.THURSDAY
                ),
                SelectedDay(
                    Calendar.FRIDAY
                ),
                SelectedDay(
                    Calendar.SATURDAY
                )
            )
        )
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card {
            /**
             * Get Time
             */
            MaterialTimePicker(
                context,
                isShowing = isTimeDialogShowing,
                is24HourField = is24HoursEnabled
            ) { selectedTime ->
                timeText = selectedTime
            }
            Column (
                modifier = Modifier
                    .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Reminder",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                HorizontalDivider()
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isTimeDialogShowing.value = true
                        }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeText,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Reminder time",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary,
                    )
                }
                HorizontalDivider()
                Column (
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Reminder type",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 15.dp)
                    )
                    Row (
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                    ) {
                        reminderTypes.forEach { type ->
                            Column (
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .background(if(selectReminderType.id == type.id) Primary else Primary.copy(0.2f))
                                    .clickable {
                                        selectReminderType = type
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = null,
                                    tint = if(selectReminderType.id == type.id) Color.White.copy(0.7f) else Primary.copy(0.6f),
                                )
                                Text(
                                    text = type.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if(selectReminderType.id == type.id) Color.White.copy(0.7f) else Primary.copy(0.6f),
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
                AnimatedVisibility(
                    visible = timeText.isNotEmpty(),
                ) {
                    Column (
                        modifier = Modifier
                            .padding(top = 10.dp)
                    ) {
                        Text(
                            text = "Reminder schedule",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 15.dp)
                        )
                        Row (
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Primary)
                        ) {
                            repeatableDays.forEachIndexed { index, day ->
                                Column (
                                    modifier = Modifier
                                        .padding(top = 1.dp, start = if(index == 0) 0.dp else 1.dp)
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if(day.isSelected) Primary else Color.White)
                                        .clickable {
                                            repeatableDays = repeatableDays.toMutableList().also {
                                                it[index] = it[index].copy(isSelected = !it[index].isSelected)
                                            }
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box (
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("EEE", Locale.ENGLISH).format(
                                                Calendar.getInstance().apply {
                                                    set(Calendar.DAY_OF_WEEK, day.day)
                                                }.time
                                            ),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if(day.isSelected) Color.White else Color.Black.copy(0.6f)
                                        )
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .height(5.dp)
                                            .fillMaxWidth()
                                            .background(if(day.isSelected) Color.White.copy(0.3f) else Primary.copy(0.2f))
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Box (
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Red,
                        )
                    }
                    VerticalDivider()
                    Box (
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                editorViewModel.repeatableDays = repeatableDays
                                editorViewModel.timeText = timeText
                                editorViewModel.selectReminderType = selectReminderType
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Confirm",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                        )
                    }
                }
            }
        }
    }
}