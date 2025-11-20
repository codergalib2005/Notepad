package com.edureminder.easynotes.presentation.screen.edit_note

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.presentation.screen.edit_note.components.AddNotesContent
import com.edureminder.easynotes.presentation.screen.edit_note.components.NoteDeletePopup
import com.edureminder.easynotes.presentation.screen.edit_note.components.ReminderType
import com.edureminder.easynotes.presentation.screen.edit_note.components.reminderTypes
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.NotepadTheme
import com.edureminder.easynotes.ui.mode.ModeViewModel
import com.edureminder.easynotes.ui.theme.ThemeViewModel
import com.edureminder.easynotes.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.easynotes.work.note.scheduleExactNoteWorker
import com.feature.edureminder.texteditor.model.rememberRichTextState
import kotlinx.coroutines.launch
import java.io.File
import kotlin.text.ifEmpty

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavHostController,
    noteID: String,
    onPDFGenerate: File
) {
    val notesViewModel: NoteViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = viewModel()

    // Observe note
    val currentNote = notesViewModel.getOneNote(noteID).collectAsState(initial = Note()).value

    val richTextState = rememberRichTextState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val isNoteDeleted = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        editorViewModel.isEditable = false
    }
    LaunchedEffect(currentNote) {
        editorViewModel.body = currentNote.body
        editorViewModel.title = currentNote.title
        richTextState.setHtml(currentNote.body)
        editorViewModel.isLocked = currentNote.isLocked
        editorViewModel.isPinned = currentNote.isFavourite
        editorViewModel.selectedFolderId = currentNote.folderId
        editorViewModel.type = currentNote.type
        editorViewModel.changeTheme(currentNote.themeId)
        editorViewModel.selectReminderType = reminderTypes.find { it.id == currentNote.reminderType } ?: reminderTypes.first()
        editorViewModel.timeText = currentNote.reminderTime
        editorViewModel.selectedFolder = folders.find { it.id == currentNote.folderId }
        editorViewModel.dateText = currentNote.reminderDate

        val dbDaysString = currentNote.repeatDays // "1,2,3,5,6,7"
        val dbDays = dbDaysString.split(",")
            .mapNotNull { it.trim().toIntOrNull() }

        editorViewModel.repeatableDays = editorViewModel.repeatableDays.map { day ->
            day.copy(isSelected = dbDays.contains(day.day))
        }
    }
    LaunchedEffect(key1 = richTextState.annotatedString) {
        editorViewModel.body = richTextState.toHtml()
    }

    fun onSaveNote(){
        coroutineScope.launch {
            saveIfChanged(
                context,
                currentNote = currentNote,
                title = editorViewModel.title,
                body = richTextState.toHtml(),
                isPinned = editorViewModel.isPinned,
                isLocked = editorViewModel.isLocked,
                folderId = editorViewModel.selectedFolder?.id ?: "0",
                themeId = editorViewModel.theme.id,
                selectReminderType = editorViewModel.selectReminderType,
                timeText = editorViewModel.timeText,
                dateText = editorViewModel.dateText,
                repeatableDays = editorViewModel.repeatableDays
            ) { note ->
                notesViewModel.upsertNote(note)
            }


            // Navigate back after saving
            navController.navigateUp()
        }
    }


    val modeViewModel = ModeViewModel(context)
    val themeViewModel = ThemeViewModel(context)
    val isDarkMode by modeViewModel.isDarkTheme.collectAsState()
    val isTheme by themeViewModel.isTheme.collectAsState()
    val settingStore = SettingsStore(context)
    val isSystemFont by settingStore.getUseSystemFontKey.collectAsState(initial = false)


    val headerColor = Color(editorViewModel.theme.headerColor.toColorInt())
    val backgroundColor = Color(editorViewModel.theme.backgroundColor.toColorInt())

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
                if (editorViewModel.theme.cat != "solid" && editorViewModel.theme.showTopLine) {
                    Box(
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
                        navController,
                        "edit_note",
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

                if (editorViewModel.isDeletePopupOpen) {
                    NoteDeletePopup(
                        editorViewModel,
                        notesViewModel,
                        listOf(noteID),
                        onEmptySelectedNotes = {
                            isNoteDeleted.value = true
                            editorViewModel.isDeletePopupOpen = false
                            navController.navigateUp()
                        },
                        onSnackbarUpdate = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        }
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isNoteDeleted.value) return@onDispose

            val selectedDaysString = editorViewModel.repeatableDays
                .filter { it.isSelected }
                .joinToString(",") { it.day.toString() }

            val isReminderChanged = editorViewModel.selectReminderType.id != currentNote.reminderType ||
                    editorViewModel.timeText != currentNote.reminderTime ||
                    currentNote.repeatDays != selectedDaysString

            val hasChanged = editorViewModel.title != currentNote.title ||
                    editorViewModel.body != currentNote.body ||
                    editorViewModel.isPinned != currentNote.isFavourite ||
                    editorViewModel.isLocked != currentNote.isLocked ||
                    editorViewModel.selectedFolder?.id != currentNote.folderId ||
                    editorViewModel.theme.id != currentNote.themeId


            if (hasChanged || isReminderChanged) {
                val updatedNote = currentNote.copy(
                    id = noteID,
                    title = editorViewModel.title.ifEmpty { "Untitled" },
                    body = editorViewModel.body,
                    isFavourite = editorViewModel.isPinned,
                    isLocked = editorViewModel.isLocked,
                    folderId = editorViewModel.selectedFolder?.id ?: "0",
                    updatedAt = System.currentTimeMillis(),
                    themeId = editorViewModel.theme.id,
                    reminderType = editorViewModel.selectReminderType.id,
                    reminderTime = editorViewModel.timeText,
                    reminderDate = editorViewModel.dateText,
                    repeatDays = selectedDaysString,
                )

                notesViewModel.upsertNote(updatedNote)

                if (isReminderChanged && updatedNote.reminderType == 2) {
                    scheduleExactNoteWorker(
                        context = context,
                        timeString = editorViewModel.timeText,
                        selectedDaysString = selectedDaysString,
                        uniqueWorkId = updatedNote.id,
                        date = editorViewModel.dateText
                    )
                } else if (isReminderChanged) {
                    cancelScheduledExactNoteWorkerIfExists(context, updatedNote.id)
                }
            }

        }
    }
}

private suspend fun saveIfChanged(
    context: Context,
    currentNote: Note,
    title: String,
    body: String,
    isPinned: Boolean,
    isLocked: Boolean,
    folderId: String,
    themeId: Int,
    selectReminderType: ReminderType,
    timeText: String,
    dateText: String,
    repeatableDays: List<SelectedDay>,
    upsert: suspend (Note) -> Unit,
) {
    val selectedDaysString = repeatableDays
        .filter { it.isSelected }
        .joinToString(",") { it.day.toString() }

    val isReminderChanged = selectReminderType.id != currentNote.reminderType ||
            timeText != currentNote.reminderTime ||
            currentNote.repeatDays != selectedDaysString

    val hasChanged = title != currentNote.title ||
            body != currentNote.body ||
            isPinned != currentNote.isFavourite ||
            isLocked != currentNote.isLocked ||
            folderId != currentNote.folderId ||
            currentNote.themeId != themeId


    if (hasChanged || isReminderChanged) {
        val updatedNote = currentNote.copy(
            id = currentNote.id,
            title = title.ifEmpty { "Untitled" },
            body = body,
            isFavourite = isPinned,
            isLocked = isLocked,
            folderId = folderId,
            updatedAt = System.currentTimeMillis(),
            themeId = themeId,
            reminderType = selectReminderType.id,
            reminderTime = timeText,
            reminderDate = dateText,
            repeatDays = selectedDaysString,
        )

        upsert(updatedNote)

        if (isReminderChanged && updatedNote.reminderType == 2) {
            scheduleExactNoteWorker(
                context = context,
                timeString = timeText,
                selectedDaysString = selectedDaysString,
                uniqueWorkId = updatedNote.id,
                date = dateText
            )
        } else if (isReminderChanged) {
            cancelScheduledExactNoteWorkerIfExists(context, updatedNote.id)
        }
    }

}