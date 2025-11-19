package com.edureminder.easynotes.presentation.screen.diary_screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import com.edureminder.easynotes.presentation.screen.diary_screen.components.BackgroundBottomSheet
import com.edureminder.easynotes.presentation.screen.diary_screen.components.EditDiaryContent
import com.edureminder.easynotes.presentation.screen.edit_note.CanvasObject
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.SelectedDay
import com.edureminder.easynotes.presentation.screen.edit_note.TextStyleSheet
import com.edureminder.easynotes.presentation.screen.edit_note.components.AddNotesContent
import com.edureminder.easynotes.presentation.screen.edit_note.components.NoteDeletePopup
import com.edureminder.easynotes.presentation.screen.edit_note.components.ReminderType
import com.edureminder.easynotes.presentation.screen.edit_note.components.reminderTypes
import com.edureminder.easynotes.room.diary.Diary
import com.edureminder.easynotes.room.diary.DiaryViewModel
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.NotepadTheme
import com.edureminder.easynotes.ui.mode.ModeViewModel
import com.edureminder.easynotes.ui.theme.ThemeViewModel
import com.edureminder.easynotes.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.easynotes.work.note.scheduleExactNoteWorker
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.text.ifEmpty

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun EditDiaryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navController: NavHostController,
    diaryID: String,
    onPDFGenerate: File
) {
    val diaryViewModel: DiaryViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = viewModel()

    // Observe note
    val currentDiary = diaryViewModel.getOneDiary(diaryID).collectAsState(initial = Diary()).value

    val richTextState = rememberRichTextState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val isNoteDeleted = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        editorViewModel.isEditable = false
    }
    LaunchedEffect(currentDiary) {
        editorViewModel.body = currentDiary.body
        editorViewModel.title = currentDiary.title
        richTextState.setHtml(currentDiary.body)
        editorViewModel.isLocked = currentDiary.isLocked
        editorViewModel.isPinned = currentDiary.isFavourite
        editorViewModel.selectedFolderId = currentDiary.folderId
        editorViewModel.changeBackground(currentDiary.backgroundId)
        editorViewModel.selectReminderType = reminderTypes.find { it.id == currentDiary.reminderType } ?: reminderTypes.first()
        editorViewModel.timeText = currentDiary.reminderTime
        editorViewModel.selectedFolder = folders.find { it.id == currentDiary.folderId }
        editorViewModel.dateText = currentDiary.reminderDate

        val dbDaysString = currentDiary.repeatDays // "1,2,3,5,6,7"
        val dbDays = dbDaysString.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
        val restoredImages = Json.decodeFromString<List<String>>(currentDiary.images)
        val restoredCanvas: List<CanvasObject> = Json.decodeFromString(currentDiary.stickers)


        editorViewModel.canvasItems = restoredCanvas
        editorViewModel.selectedImages = restoredImages
        editorViewModel.repeatableDays = editorViewModel.repeatableDays.map { day ->
            day.copy(isSelected = dbDays.contains(day.day))
        }
    }
    LaunchedEffect(key1 = richTextState.annotatedString) {
        editorViewModel.body = richTextState.toHtml()
    }

    fun onSaveDiary(){
        coroutineScope.launch {
            saveIfChanged(
                context,
                currentDiary = currentDiary,
                title = editorViewModel.title,
                body = richTextState.toHtml(),
                isPinned = editorViewModel.isPinned,
                isLocked = editorViewModel.isLocked,
                folderId = editorViewModel.selectedFolder?.id ?: "0",
                canvasItems = editorViewModel.canvasItems,
                selectedImages = editorViewModel.selectedImages,
                backgroundId = editorViewModel.selectedBackground.id,
                selectReminderType = editorViewModel.selectReminderType,
                timeText = editorViewModel.timeText,
                dateText = editorViewModel.dateText,
                repeatableDays = editorViewModel.repeatableDays
            ) { diary ->
                diaryViewModel.upsertDiary(diary)
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (editorViewModel.selectedBackground.id != 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        Image(
                            painter = painterResource(id = editorViewModel.selectedBackground.res),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.FillWidth,
                            alignment = Alignment.BottomCenter
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    EditDiaryContent(
                        sharedTransitionScope,
                        animatedContentScope,
                        richTextState,
                        navController,
                        "edit_diary",
                        onPDFGenerate,
                        editorViewModel,
                        folders,
                        folderViewModel,
                        onSaveDiary = ::onSaveDiary,
                        backgroundColor,
                        onSnackbarUpdate = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                    )
                }

                BackgroundBottomSheet(
                    sheetState = sheetState,
                    onDismiss = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    },
                    editorViewModel
                )

//                if (editorViewModel.isDeletePopupOpen) {
//                    NoteDeletePopup(
//                        editorViewModel,
//                        notesViewModel,
//                        listOf(noteID),
//                        onEmptySelectedNotes = {
//                            isNoteDeleted.value = true
//                            editorViewModel.isDeletePopupOpen = false
//                            navController.navigateUp()
//                        },
//                        onSnackbarUpdate = {
//                            scope.launch {
//                                snackbarHostState.showSnackbar(it)
//                            }
//                        }
//                    )
//                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isNoteDeleted.value) return@onDispose

            val selectedDaysString = editorViewModel.repeatableDays
                .filter { it.isSelected }
                .joinToString(",") { it.day.toString() }
            val images = Json.encodeToString(editorViewModel.selectedImages)
            val canvas = Json.encodeToString(editorViewModel.canvasItems)

            val isReminderChanged = editorViewModel.selectReminderType.id != currentDiary.reminderType ||
                    editorViewModel.timeText != currentDiary.reminderTime ||
                    currentDiary.repeatDays != selectedDaysString

            val hasChanged = editorViewModel.title != currentDiary.title ||
                    editorViewModel.body != currentDiary.body ||
                    editorViewModel.isPinned != currentDiary.isFavourite ||
                    editorViewModel.isLocked != currentDiary.isLocked ||
                    editorViewModel.selectedFolder?.id != currentDiary.folderId ||
                    editorViewModel.selectedBackground.id != currentDiary.backgroundId ||
                    canvas != currentDiary.stickers ||
                    images != currentDiary.images



            if (hasChanged || isReminderChanged) {
                val updatedNote = currentDiary.copy(
                    id = currentDiary.id,
                    title = editorViewModel.title.ifEmpty { "Untitled" },
                    body = editorViewModel.body,
                    isFavourite = editorViewModel.isPinned,
                    isLocked = editorViewModel.isLocked,
                    folderId = editorViewModel.selectedFolder?.id ?: "0",
                    images = images,
                    stickers = canvas,

                    updatedAt = System.currentTimeMillis(),
                    backgroundId = editorViewModel.selectedBackground.id,
                    reminderType = editorViewModel.selectReminderType.id,
                    reminderTime = editorViewModel.timeText,
                    reminderDate = editorViewModel.dateText,
                    repeatDays = selectedDaysString,
                )

                diaryViewModel.upsertDiary(updatedNote)

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
    currentDiary: Diary,
    title: String,
    body: String,
    isPinned: Boolean,
    isLocked: Boolean,
    folderId: String,
    backgroundId: Int,
    canvasItems: List<CanvasObject>,
    selectedImages: List<String>,
    selectReminderType: ReminderType,
    timeText: String,
    dateText: String,
    repeatableDays: List<SelectedDay>,
    upsert: suspend (Diary) -> Unit,
) {
    val selectedDaysString = repeatableDays
        .filter { it.isSelected }
        .joinToString(",") { it.day.toString() }
    val images = Json.encodeToString(selectedImages)
    val canvas = Json.encodeToString(canvasItems)

    val isReminderChanged = selectReminderType.id != currentDiary.reminderType ||
            timeText != currentDiary.reminderTime ||
            currentDiary.repeatDays != selectedDaysString

    val hasChanged = title != currentDiary.title ||
            body != currentDiary.body ||
            isPinned != currentDiary.isFavourite ||
            isLocked != currentDiary.isLocked ||
            folderId != currentDiary.folderId ||
            currentDiary.backgroundId != backgroundId ||
            canvas != currentDiary.stickers ||
            images != currentDiary.images



    if (hasChanged || isReminderChanged) {
        val updatedNote = currentDiary.copy(
            id = currentDiary.id,
            title = title.ifEmpty { "Untitled" },
            body = body,
            isFavourite = isPinned,
            isLocked = isLocked,
            folderId = folderId,
            images = images,
            stickers = canvas,

            updatedAt = System.currentTimeMillis(),
            backgroundId = backgroundId,
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