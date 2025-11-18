package com.edureminder.easynotes.presentation.screen.diary_screen.components

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
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
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.lib.richeditor.RichTextStyleButton
import com.edureminder.easynotes.presentation.components.MaterialTimePicker
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor.ImagePickerButton
import com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor.MarkdownDiaryContent
import com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor.TextStyleSheet
import com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor.ThumbnailImage
import com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor.fontSizes
import com.edureminder.easynotes.presentation.screen.edit_note.CanvasObject
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.SelectedDay
import com.edureminder.easynotes.presentation.screen.edit_note.components.MoreOption
import com.edureminder.easynotes.presentation.screen.edit_note.components.TextEditorLinkDialog
import com.edureminder.easynotes.presentation.screen.edit_note.components.generatePDF
import com.edureminder.easynotes.presentation.screen.folder_screen.components.AddOrRenameFolderPopupOpen
import com.edureminder.easynotes.room.folder.Folder
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.utils.copyUriToInternalStorage
import com.edureminder.easynotes.viewmodels.MainViewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.paragraph.type.UnorderedListStyleType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.forEach
import kotlin.compareTo
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun EditDiaryContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
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
    val plainText = richTextState.toText().toString()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val baseLimit = /**if (isPro) 10 else 15 */ if (isPro) 50000 else 5000
    val maxAllowedLength = baseLimit + 500  // Let them finish naturally
    val theme = editorViewModel.theme
    val titleFocusRequester = remember { FocusRequester() }
    val editorFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val openLinkDialog = remember { mutableStateOf(false) }
    val isMaxLengthReached by remember(plainText, isPro) {
        derivedStateOf { plainText.length >= baseLimit }
    }


    LaunchedEffect(plainText, isPro) {
        if (plainText.length > maxAllowedLength) {
            // Just cut off extra characters
            val truncated = plainText.take(maxAllowedLength)
            richTextState.setText(truncated)
        }
    }


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


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .statusBarsPadding()
        ) {
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
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
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
                                tint = Color.Black,
                            )
                        }
                        if(editorViewModel.isEditable) Box (
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Primary)
                                .clickable {
                                    onSaveNote()
                                    editorViewModel.isEditable = false
                                }
                                .padding(horizontal = 13.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Done",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
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
                                    tint = Color.Black,
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
                                    tint = Color.Black,
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

        Column(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()

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
                                editorViewModel.isOpenFolderList =
                                    !editorViewModel.isOpenFolderList
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
                            imageVector = if (editorViewModel.isOpenFolderList) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            tint = Color.Gray,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        if (editorViewModel.isOpenFolderList) FolderList(
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
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !editorViewModel.isEditable
                    ){
                        editorViewModel.isEditable = true
                    }
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                AnimatedCursorTextField(
                    text = editorViewModel.title,
                    onTextChange = {
                        if (it.length <= 65) handleTextChange(it)
                    },
                    hint = "Enter title...",
                    enabled = editorViewModel.isEditable
                )
                MarkdownDiaryContent(
                    richTextState,
                    editorFocusRequester,
                    theme,
                    editorViewModel,
                    backgroundColor,
                    navController,
                    mainViewModel,
                )
                LazyRow(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .zIndex(5f),
                    contentPadding = PaddingValues(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(6) {
                        ThumbnailImage(
                            sharedTransitionScope,
                            animatedContentScope,
                            "/data/user/0/com.edureminder.easynotes/files/images/img_1763474113043.jpg",
                            onClick = {
                                navController.navigate(Screen.ImageViewScreen("/data/user/0/com.edureminder.easynotes/files/images/img_1763474113043.jpg"))
                            }
                        )
                    }
                }

                InfiniteCanvas(
                    editorViewModel = editorViewModel
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    // Top layer: Color picker
                    TextStyleSheet(
                        richTextState = richTextState,
                        editorViewModel = editorViewModel,
                        fontSizes = fontSizes
                    )



                    androidx.compose.animation.AnimatedVisibility(
                        visible = openLinkDialog.value,
                        modifier = Modifier
                            .zIndex(2f)
                            .fillMaxWidth()
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                openLinkDialog.value = false
                            }
                    ) {
                        TextEditorLinkDialog(
                            state = richTextState,
                            openLinkDialog = openLinkDialog
                        )
                    }

                    Column {
                        AnimatedVisibility(
                            visible = isMaxLengthReached,
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
                                            text = "Upgrade Pro for more space",
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
                                                    navController.navigate(Screen.Subscription) {
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
                                            text = "Max characters reached",
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

                        Row(
                            modifier = Modifier
                                .zIndex(1f)
                                .fillMaxWidth()
                                .background(Color.White)
                                .background(Primary.copy(0.1f))
                                .padding(start = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        editorViewModel.showThemeSheet = true
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.palette),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp)
                                )
                            }
                            Box(
                                Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(Color(0x48393B3D))
                            )
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                Box(
                                    Modifier
                                        .height(24.dp)
                                        .width(1.dp)
                                        .background(Color(0x48393B3D))
                                )
                                RichTextStyleButton(
                                    onClick = {
                                        openLinkDialog.value = true
                                    },
                                    isSelected = richTextState.isLink,
                                    icon = Icons.Outlined.Link
                                )
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            editorViewModel.isTextFontSizePickerOpen =
                                                !editorViewModel.isTextFontSizePickerOpen
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFields,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(23.dp)
                                    )
                                }
                                ImagePickerButton { uri ->
                                    val copiedFile = copyUriToInternalStorage(context, uri)
                                    copiedFile?.let {
                                        Log.d("Log1", "Copied image path: ${it.absolutePath}")
                                        // You can store `it.absolutePath` in your DB or ViewModel
                                    }
                                }
                                Box(
                                    Modifier
                                        .height(24.dp)
                                        .width(1.dp)
                                        .background(Color(0x48393B3D))
                                )

                                RichTextStyleButton(
                                    onClick = {
                                        scope.launch {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()

                                            delay(100)
                                            editorViewModel.isListSelectorSheetOpen = true
                                        }
                                    },
                                    isSelected = richTextState.isUnorderedList,
                                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
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

    ListStyleSheet(
        richTextState,
        editorViewModel,
        sheetState,
        onOpenKeyboard = {
            scope.launch {
                editorViewModel.isListSelectorSheetOpen = false // close sheet

                delay(120)                         // Wait for sheet to hide animation
                editorFocusRequester.requestFocus()      // Restore focus to editor

                keyboardController?.show()
            }
        }
    )
}
@Composable
fun InfiniteCanvas(
    editorViewModel: NoteEditorViewModel
) {
    // HUGE height so items can be moved anywhere
    // You can use 5000.dp or calculate dynamically.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4000.dp)  // 🔥 The trick: large invisible canvas
            .background(Color.Transparent)
    ) {
        editorViewModel.canvasItems.forEach { item ->
            CanvasItem(
                item = item,
                onUpdate = {  editorViewModel.updateItem(it) },
                onDelete = { editorViewModel.deleteItem(it) }
            )
        }
    }
}
@Composable
fun CanvasItem(
    item: CanvasObject,
    onUpdate: (CanvasObject) -> Unit,
    onDelete: (CanvasObject) -> Unit
) {
    var offset by remember { mutableStateOf(item.offset) }
    var rotation by remember { mutableStateOf(item.rotation) }
    var scale by remember { mutableStateOf(item.scale) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .graphicsLayer(
                rotationZ = rotation,
                scaleX = scale,
                scaleY = scale
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset += dragAmount
                    onUpdate(item.copy(offset = offset))
                }
            }
    ) {
        Image(
            painter = painterResource(id = item.res),
            contentDescription = null,
            modifier = Modifier.size(140.dp)
        )

        // close
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(32.dp)
                .clickable { onDelete(item) }
        )

        // rotate
        Icon(
            imageVector = Icons.Default.RotateRight,
            contentDescription = "",
            tint = Color.Magenta,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        rotation += dragAmount.x
                        onUpdate(item.copy(rotation = rotation))
                    }
                }
        )

        // scale
        Icon(
            imageVector = Icons.Default.Scale,
            contentDescription = "",
            tint = Color.Blue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        scale += dragAmount.x * 0.01f
                        scale = scale.coerceIn(0.5f, 3f)
                        onUpdate(item.copy(scale = scale))
                    }
                }
        )
    }
}

@Composable
fun AnimatedCursorTextField(
    text: String,
    onTextChange: (String) -> Unit,
    hint: String = "Enter text...",
    enabled: Boolean = true
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        ),
        enabled = enabled,
        cursorBrush = SolidColor(Primary.copy(0.7f)), // We'll draw our custom cursor
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                innerTextField()
            }
        },
        modifier = Modifier
            .padding(horizontal = 10.dp)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListStyleSheet(
    richTextState: RichTextState,
    editorViewModel: NoteEditorViewModel,
    sheetState: SheetState,
    onOpenKeyboard: () -> Unit
){
    val lists = listOf("•","➔","✓","\uD83D\uDD25","🍉","⭐","⬛","\uD83D\uDC49","🍎","❤️","🍒","🍓")

    if(editorViewModel.isListSelectorSheetOpen){
        ModalBottomSheet(
            onDismissRequest = {
                onOpenKeyboard()
            },
            dragHandle = {

            },
            shape = RoundedCornerShape(0.dp),
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(30.dp))
                    Text(
                        text = "List",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = {
                            onOpenKeyboard()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                }
                FlowRow(
                    maxItemsInEachRow = 3,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = if(richTextState.isOrderedList) Primary else Color.LightGray,
                                shape = MaterialTheme.shapes.small
                            )
                            .weight(1f)
                            .height(60.dp)
                            .clickable {
                                richTextState.toggleOrderedList()
                                editorViewModel.selectedEmoji = ""
                            }
                            .padding(vertical = 7.dp, horizontal = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(1, 2, 3).forEach { item ->
                            Row (
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = item.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End
                                )
                                Spacer(
                                    modifier = Modifier
                                        .padding(
                                            end = when(item){
                                                2 -> 10.dp
                                                3 -> 15.dp
                                                else -> 0.dp
                                            },
                                            start = if(item == 1) 3.dp else 0.dp)
                                        .weight(1f)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray.copy(0.7f))
                                )
                            }
                        }
                    }
                    lists.forEach { emoji ->
                        val currentType = richTextState.config.unorderedListStyleType
                        val newType = UnorderedListStyleType.from(emoji)

                        Column(
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = if(editorViewModel.selectedEmoji == emoji) Primary else Color.LightGray,
                                    shape = MaterialTheme.shapes.small
                                )
                                .weight(1f)
                                .height(60.dp)
                                .clickable {

                                    if (currentType == newType) {

                                        // Same emoji → turn OFF
                                        richTextState.toggleUnorderedList()

                                        // CLEAR selected emoji
                                        editorViewModel.selectedEmoji = ""

                                    } else {

                                        // Save selected emoji
                                        editorViewModel.selectedEmoji = emoji

                                        // Reset existing list first
                                        if (richTextState.isUnorderedList) {
                                            richTextState.toggleUnorderedList()
                                        }

                                        // Set bullet type
                                        richTextState.config.unorderedListStyleType = newType

                                        // Turn list back ON
                                        richTextState.toggleUnorderedList()
                                    }
                                }
                                .padding(vertical = 7.dp, horizontal = 10.dp)
                        ) {
                            listOf(0, 1, 2).forEach { item ->
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = emoji,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .padding(end =when(item){
                                                1 -> 10.dp
                                                2 -> 15.dp
                                                else -> 0.dp
                                            })
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray.copy(0.7f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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