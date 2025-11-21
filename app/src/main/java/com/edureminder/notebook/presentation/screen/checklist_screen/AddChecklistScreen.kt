package com.edureminder.notebook.presentation.screen.checklist_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edureminder.notebook.datastore.SettingsStore
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.presentation.screen.edit_note.TextStyleSheet
import com.edureminder.notebook.room.folder.FolderViewModel
import com.edureminder.notebook.room.note.Note
import com.edureminder.notebook.room.note.NoteViewModel
import com.edureminder.notebook.room.note.Type
import com.edureminder.notebook.ui.ColorBlack
import com.edureminder.notebook.ui.NotepadTheme
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.ui.mode.ModeViewModel
import com.edureminder.notebook.ui.theme.ThemeViewModel
import com.edureminder.notebook.utils.getCurrentDate
import com.edureminder.notebook.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.notebook.work.note.scheduleExactNoteWorker
import kotlinx.coroutines.launch
import java.io.File
import kotlin.text.ifEmpty
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChecklistScreen(
    navController: NavController,
    onPDFGenerate: File,
) {
    val context = LocalContext.current
    val notesViewModel: NoteViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = viewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var hasSaved by remember { mutableStateOf(false) }

    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())

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
                body = Json.encodeToString(editorViewModel.checklist),
                isFavourite = editorViewModel.isPinned,
                isLocked = editorViewModel.isLocked,
                folderId = editorViewModel.selectedFolder?.id ?: "0",
                type = Type.CHECKLIST,
                themeId = editorViewModel.theme.id,
                reminderTime = editorViewModel.timeText,
                reminderType = editorViewModel.selectReminderType.id,
                repeatDays = selectedDaysString,
                reminderDate = editorViewModel.dateText
            )
            notesViewModel.upsertNote(newNote)
            hasSaved = true
            navController.navigateUp()

            if(newNote.reminderType == 2) {
                // ✅ Start WorkManager
                scheduleExactNoteWorker(
                    context = context,
                    timeString = editorViewModel.timeText,
                    selectedDaysString = selectedDaysString,
                    uniqueWorkId = newNote.id,
                    date = getCurrentDate()
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
            containerColor = Primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AddChecklistContent(
                    navController,
                    "add_note",
                    onPDFGenerate,
                    editorViewModel,
                    folders,
                    notesViewModel,
                    folderViewModel,
                    onSaveNote = ::onSaveNote,
                    onSnackbarUpdate = { message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    },
                )
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
                    body = Json.encodeToString(editorViewModel.checklist),
                    isFavourite = editorViewModel.isPinned,
                    isLocked = editorViewModel.isLocked,
                    folderId = editorViewModel.selectedFolder?.id ?: "0",
                    type = Type.CHECKLIST,
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
                        date = getCurrentDate()
                    )
                } else {
                    cancelScheduledExactNoteWorkerIfExists(context, newNote.id)
                }
            }
        }
    }
}