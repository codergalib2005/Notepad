package com.edureminder.notebook.presentation.screen.diary_screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.notebook.datastore.SettingsStore
import com.edureminder.notebook.presentation.screen.diary_screen.components.BackgroundBottomSheet
import com.edureminder.notebook.presentation.screen.diary_screen.components.EditDiaryContent
import com.edureminder.notebook.presentation.screen.edit_note.CanvasObject
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.presentation.screen.edit_note.SelectedDay
import com.edureminder.notebook.presentation.screen.edit_note.components.ReminderType
import com.edureminder.notebook.presentation.screen.edit_note.components.reminderTypes
import com.edureminder.notebook.room.diary.Diary
import com.edureminder.notebook.room.diary.DiaryViewModel
import com.edureminder.notebook.room.folder.FolderViewModel
import com.edureminder.notebook.ui.NotepadTheme
import com.edureminder.notebook.ui.mode.ModeViewModel
import com.edureminder.notebook.ui.theme.ThemeViewModel
import com.edureminder.notebook.utils.safeDecodeList
import com.edureminder.notebook.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.notebook.work.note.scheduleExactNoteWorker
import com.feature.edureminder.texteditor.model.rememberRichTextState
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

    val currentDiary by diaryViewModel.getOneDiary(diaryID).collectAsState(initial = Diary())
    val richTextState = rememberRichTextState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val isNoteDeleted = remember { mutableStateOf(false) }
    val editableDiary = remember { mutableStateOf(currentDiary) }
    val diaryToSave = remember { mutableStateOf(currentDiary) }

    // Initialize editor state when diary is loaded
    LaunchedEffect(currentDiary) {
        diaryToSave.value = currentDiary
        editableDiary.value = currentDiary
        editorViewModel.apply {
            title = currentDiary.title
            body = currentDiary.body
            isPinned = currentDiary.isFavourite
            isLocked = currentDiary.isLocked
            selectedFolderId = currentDiary.folderId
            selectedFolder = folders.find { it.id == currentDiary.folderId }
            changeBackground(currentDiary.backgroundId)
            selectReminderType = reminderTypes.find { it.id == currentDiary.reminderType } ?: reminderTypes.first()
            timeText = currentDiary.reminderTime
            dateText = currentDiary.reminderDate
            repeatableDays = repeatableDays.map { day ->
                day.copy(isSelected = currentDiary.repeatDays.split(",").mapNotNull { it.toIntOrNull() }.contains(day.day))
            }
            selectedImages = safeDecodeList(currentDiary.images)
            canvasItems = safeDecodeList(currentDiary.stickers)
        }
        richTextState.setHtml(currentDiary.body)
    }

    // Update body HTML whenever richTextState changes
    LaunchedEffect(richTextState.annotatedString) {
        editorViewModel.body = richTextState.toHtml()
    }

    fun onSaveDiary() {
        val selectedDaysString = editorViewModel.repeatableDays
            .filter { it.isSelected }
            .joinToString(",") { it.day.toString() }

        val images = Json.encodeToString(editorViewModel.selectedImages)
        val canvas = Json.encodeToString(editorViewModel.canvasItems)

        val updatedDiary = editableDiary.value.copy(
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
            repeatDays = selectedDaysString
        )

        diaryViewModel.upsertDiary(updatedDiary)
    }

    val modeViewModel = ModeViewModel(context)
    val themeViewModel = ThemeViewModel(context)
    val isDarkMode by modeViewModel.isDarkTheme.collectAsState()
    val isTheme by themeViewModel.isTheme.collectAsState()
    val settingStore = SettingsStore(context)
    val isSystemFont by settingStore.getUseSystemFontKey.collectAsState(initial = false)

    val headerColor = Color(editorViewModel.theme.headerColor.toColorInt())
    val backgroundColor = Color(editorViewModel.theme.backgroundColor.toColorInt())
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        // Save diary before navigating back
        coroutineScope.launch {
            saveIfChanged(
                context = context,
                currentDiary = diaryToSave.value,
                title = editorViewModel.title,
                body = editorViewModel.body,
                isPinned = editorViewModel.isPinned,
                isLocked = editorViewModel.isLocked,
                folderId = editorViewModel.selectedFolder?.id ?: "0",
                backgroundId = editorViewModel.selectedBackground.id,
                canvasItems = editorViewModel.canvasItems,
                selectedImages = editorViewModel.selectedImages,
                selectReminderType = editorViewModel.selectReminderType,
                timeText = editorViewModel.timeText,
                dateText = editorViewModel.dateText,
                repeatableDays = editorViewModel.repeatableDays
            ) { updatedDiary ->
                diaryViewModel.upsertDiary(updatedDiary)
            }

            navController.navigateUp() // Navigate back after save
        }
    }

    NotepadTheme(
        isTheme = isTheme,
        dynamicColor = true,
        useSystemFont = isSystemFont,
        isDarkMode = isDarkMode,
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = headerColor
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                if (editorViewModel.selectedBackground.id != 1) {
                    Image(
                        painter = painterResource(id = editorViewModel.selectedBackground.res),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                        contentScale = ContentScale.FillWidth,
                        alignment = Alignment.BottomCenter
                    )
                }

                Column(modifier = Modifier.fillMaxSize()) {
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
                            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
                        },
                    )
                }

                BackgroundBottomSheet(
                    sheetState = sheetState,
                    onDismiss = { coroutineScope.launch { sheetState.hide() } },
                    editorViewModel
                )
            }
        }
    }

//    DisposableEffect(Unit) {
//        onDispose {
//            if (isNoteDeleted.value) return@onDispose
//
//            // Use latest snapshot
//            val latestDiary = diaryToSave.value
//
//            coroutineScope.launch {
//                saveIfChanged(
//                    context = context,
//                    currentDiary = latestDiary,
//                    title = editorViewModel.title,
//                    body = editorViewModel.body,
//                    isPinned = editorViewModel.isPinned,
//                    isLocked = editorViewModel.isLocked,
//                    folderId = editorViewModel.selectedFolder?.id ?: "0",
//                    backgroundId = editorViewModel.selectedBackground.id,
//                    canvasItems = editorViewModel.canvasItems,
//                    selectedImages = editorViewModel.selectedImages,
//                    selectReminderType = editorViewModel.selectReminderType,
//                    timeText = editorViewModel.timeText,
//                    dateText = editorViewModel.dateText,
//                    repeatableDays = editorViewModel.repeatableDays
//                ) { updatedDiary ->
//                    diaryViewModel.upsertDiary(updatedDiary)
//                }
//            }
//        }
//    }
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
    val selectedDaysString = repeatableDays.filter { it.isSelected }.joinToString(",") { it.day.toString() }
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
            backgroundId != currentDiary.backgroundId ||
            canvas != currentDiary.stickers ||
            images != currentDiary.images

    if (hasChanged || isReminderChanged) {
        val updatedDiary = currentDiary.copy(
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
            repeatDays = selectedDaysString
        )

        upsert(updatedDiary)

        if (isReminderChanged) {
            if (updatedDiary.reminderType == 2) {
                scheduleExactNoteWorker(context, timeText, selectedDaysString, updatedDiary.id, dateText)
            } else {
                cancelScheduledExactNoteWorkerIfExists(context, updatedDiary.id)
            }
        }
    }
}
