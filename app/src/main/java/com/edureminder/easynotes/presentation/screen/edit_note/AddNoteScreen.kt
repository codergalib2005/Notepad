package com.edureminder.easynotes.presentation.screen.edit_note

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.easynotes.data.Theme
import com.edureminder.easynotes.data.ThemeManager
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.presentation.screen.edit_note.components.AddNotesContent
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.room.note.Type
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.NotepadTheme
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.ui.mode.ModeViewModel
import com.edureminder.easynotes.ui.theme.ThemeViewModel
import com.edureminder.easynotes.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.easynotes.work.note.scheduleExactNoteWorker
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.launch
import java.io.File



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    navHostController: NavHostController,
    onPDFGenerate: File,
) {
    val context = LocalContext.current
    val notesViewModel: NoteViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = viewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val richTextState = rememberRichTextState()
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var hasSaved by remember { mutableStateOf(false) }

    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = richTextState.annotatedString) {
        editorViewModel.body = richTextState.toHtml()
    }

    LaunchedEffect(folders) {
        if(folders.isNotEmpty()){
            editorViewModel.selectedFolder = folders.first()
        }
    }

    fun onSaveNote() {
        if (hasSaved) return
        coroutineScope.launch {

            val selectedDaysString = editorViewModel.repeatableDays
                .filter { it.isSelected }
                .joinToString(",") { it.day.toString() }

            val newNote = Note(
                title = editorViewModel.title.ifEmpty { "Untitled" },
                body = richTextState.toHtml(),
                isFavourite = editorViewModel.isPinned,
                isLocked = editorViewModel.isLocked,
                folderId = editorViewModel.selectedFolder?.id ?: "0",
                type = Type.NOTE,
                themeId = editorViewModel.theme.id,
                reminderTime = editorViewModel.timeText,
                reminderDate = editorViewModel.dateText,
                reminderType = editorViewModel.selectReminderType.id,
                repeatDays = selectedDaysString,
            )
            notesViewModel.upsertNote(newNote)
            hasSaved = true
            navHostController.navigateUp()

            if(newNote.reminderType == 2) {
                // ✅ Start WorkManager
                scheduleExactNoteWorker(
                    context = context,
                    timeString = editorViewModel.timeText,
                    selectedDaysString = selectedDaysString,
                    uniqueWorkId = newNote.id,
                    date = editorViewModel.dateText
                )
            } else {
                cancelScheduledExactNoteWorkerIfExists(context, newNote.id)
            }
        }
    }

    val modeViewModel = ModeViewModel(context)
    val themeViewModel = ThemeViewModel(context)
    val isDarkMode by modeViewModel.isDarkTheme.collectAsState()
    val isTheme by themeViewModel.isTheme.collectAsState()
    val settingStore = SettingsStore(context)
    val isSystemFont by settingStore.getUseSystemFontKey.collectAsState(initial = false)

    val headerColor = Color(editorViewModel.theme.headerColor.toColorInt())
    val backgroundColor = if(editorViewModel.theme.cat == "solid")  Color(editorViewModel.theme.headerColor.toColorInt()).copy(0.2f) else Color(editorViewModel.theme.backgroundColor.toColorInt())

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NotepadTheme(
        isTheme = isTheme,
        dynamicColor = true,
        useSystemFont = isSystemFont,
        isDarkMode = isDarkMode,
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = headerColor
        ) {
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if(editorViewModel.theme.cat != "solid" && editorViewModel.theme.showTopLine){
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .padding(bottom = 40.dp)
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
                        .fillMaxSize()
                ) {
                    AddNotesContent(
                        richTextState,
                        navHostController,
                        "add_note",
                        onPDFGenerate,
                        editorViewModel,
                        folders,
                        folderViewModel,
                        onSaveNote = ::onSaveNote,
                        backgroundColor,
                        onSnackbarUpdate = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                    )
                }
            }

            if (editorViewModel.showThemeSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        editorViewModel.showThemeSheet = false
                    },
                    sheetState = sheetState,
                    dragHandle = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(Modifier.size(30.dp).semantics {
                                contentDescription = "space box"
                            })
                            Text(
                                text = "Theme & Color",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W600,
                                color = ColorBlack.copy(0.7f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        editorViewModel.showThemeSheet = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(23.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    scrimColor = Color.Transparent,
                    shape = MaterialTheme.shapes.large,
                    containerColor = Color.White,
                ) {
                    TextStyleSheet(
                        editorViewModel
                    )
                }
            }
        }
    }



    DisposableEffect(Unit) {
        onDispose {
            if (!hasSaved) {
                val selectedDaysString = editorViewModel.repeatableDays
                    .filter { it.isSelected }
                    .joinToString(",") { it.day.toString() }

                val newNote = Note(
                    title = editorViewModel.title.ifEmpty { "Untitled" },
                    body = richTextState.toHtml(),
                    isFavourite = editorViewModel.isPinned,
                    isLocked = editorViewModel.isLocked,
                    folderId = editorViewModel.selectedFolder?.id ?: "0",
                    type = Type.NOTE,
                    themeId = editorViewModel.theme.id,
                    reminderTime = editorViewModel.timeText,
                    reminderDate = editorViewModel.dateText,
                    reminderType = editorViewModel.selectReminderType.id,
                    repeatDays = selectedDaysString
                )

                // ✅ Insert or update note
                notesViewModel.upsertNote(newNote)
                hasSaved = true


                if(newNote.reminderType == 2) {
                    // ✅ Start WorkManager
                    scheduleExactNoteWorker(
                        context = context,
                        timeString = editorViewModel.timeText,
                        selectedDaysString = selectedDaysString,
                        uniqueWorkId = newNote.id,
                        date = editorViewModel.dateText
                    )
                } else {
                    cancelScheduledExactNoteWorkerIfExists(context, newNote.id)
                }
            }
        }
    }

}


@OptIn(ExperimentalRichTextApi::class)
@Composable
fun TextStyleSheet(
    editorViewModel: NoteEditorViewModel,
){
    val themes = ThemeManager.getThemesByCategory(editorViewModel.selectedCategory.lowercase().replace(" ", ""))

    val currentIndex = ThemeManager.tabs.indexOf(editorViewModel.selectedCategory).takeIf { it != -1 } ?: 0

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = currentIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                val currentTab = tabPositions[currentIndex]
                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomStart)
                        .offset(x = currentTab.left)
                        .width(currentTab.width)
                        .height(2.dp)
                        .background(Primary, RoundedCornerShape(1.dp))
                )
            },
            divider = { Spacer(Modifier.size(0.dp)) },
            tabs = {
                ThemeManager.tabs.forEachIndexed { index, title ->
                    val thisIndex = currentIndex == index
                    Tab(
                        selected = thisIndex,
                        onClick = {
                            editorViewModel.selectedCategory = title
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (thisIndex) FontWeight.W800 else FontWeight.W400,
                                color = if (thisIndex) Primary else ColorBlack.copy(0.6f),
                                textAlign = TextAlign.Start
                            )
                        },
                    )
                }
            }
        )
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {}
            items(themes) { noteTheme ->
                ColorNoteItem(
                    noteTheme,
                    onChangeTheme = {
                        editorViewModel.changeTheme(it.id)
                    },
                    editorViewModel
                )
            }
            item {}
        }
    }
}

@Composable
fun ColorNoteItem(
    noteTheme: Theme,
    onChangeTheme: (Theme) -> Unit,
    editorViewModel: NoteEditorViewModel
) {
    Log.d("Log1", "theme -> ${noteTheme}")
    Column (
        modifier = Modifier
            .height(170.dp)
            .width(100.dp)
            .border(
                width = 2.dp,
                color = if(editorViewModel.theme.id == noteTheme.id) Primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){
                onChangeTheme(noteTheme)
            },
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(Color(noteTheme.headerColor.toColorInt()))
        ) {
            if(noteTheme.cat != "solid" && !noteTheme.showTopLine){
                Image(
                    painter = painterResource(id = noteTheme.value),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    alignment = Alignment.TopStart
                )
            }
        }
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(noteTheme.backgroundColor.toColorInt())),
        ) {
            if(noteTheme.cat == "solid"){
                Box (
                    modifier = Modifier
                        .height(160.dp)
                        .width(100.dp)
                ) {
                    Text(
                        text = "NONE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.Black.copy(0.5f),
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                if(noteTheme.showTopLine){
                    Image(
                        painter = painterResource(id = noteTheme.value),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                        alignment = Alignment.TopStart
                    )
                }
            }
        }
    }
}