package com.edureminder.easynotes.presentation.screen.diary_screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.presentation.screen.diary_screen.components.BackgroundBottomSheet
import com.edureminder.easynotes.presentation.screen.diary_screen.components.EditDiaryContent
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.room.diary.Diary
import com.edureminder.easynotes.room.diary.DiaryViewModel
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.room.note.Type
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.NotepadTheme
import com.edureminder.easynotes.ui.mode.ModeViewModel
import com.edureminder.easynotes.ui.theme.ThemeViewModel
import com.edureminder.easynotes.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.easynotes.work.note.scheduleExactNoteWorker
import com.feature.edureminder.texteditor.model.rememberRichTextState
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.text.ifEmpty

data class Mode(
    val id: Int,
    val icon: Int,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@SuppressLint("ViewModelConstructorInComposable", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddDiaryScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navController: NavHostController,
    onPDFGenerate: File
) {
    val moods = listOf(
        Mode(1, R.drawable.mood_1),
        Mode(2, R.drawable.mood_2),
        Mode(3, R.drawable.mood_3),
        Mode(4, R.drawable.mood_4),
        Mode(5, R.drawable.mood_5),
    )


    val context = LocalContext.current
    val diaryViewModel: DiaryViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = viewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val richTextState = rememberRichTextState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newState ->
            // Block hiding by user drag
            newState != SheetValue.Hidden
        }
    )
    val coroutineScope = rememberCoroutineScope()
    var hasSaved by remember { mutableStateOf(false) }

    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = richTextState.annotatedString) {
        editorViewModel.body = richTextState.toHtml()
    }

    LaunchedEffect(folders) {
        if (folders.isNotEmpty()) {
            editorViewModel.selectedFolder = folders.first()
        }
    }

    fun onSaveDiary() {
        if (hasSaved) return
        coroutineScope.launch {

            val selectedDaysString = editorViewModel.repeatableDays
                .filter { it.isSelected }
                .joinToString(",") { it.day.toString() }
            val images = Json.encodeToString(editorViewModel.selectedImages)
            val canvas = Json.encodeToString(editorViewModel.canvasItems)
//            val rawBody = richTextState.toString() // contains text exactly as typed, including emojis

            val newDiary = Diary(
                title = editorViewModel.title.ifEmpty { "Untitled" },
                body = richTextState.toHtml(),
                isFavourite = editorViewModel.isPinned,
                isLocked = editorViewModel.isLocked,
                folderId = editorViewModel.selectedFolder?.id ?: "0",
//                mood = editorViewModel.selectedMood.id,
                backgroundId = editorViewModel.selectedBackground.id,
                images = images,
                stickers = canvas,
                reminderTime = editorViewModel.timeText,
                reminderDate = editorViewModel.dateText,
                reminderType = editorViewModel.selectReminderType.id,
                repeatDays = selectedDaysString,
            )
            diaryViewModel.upsertDiary(newDiary)
            hasSaved = true
            navController.navigateUp()

            if (newDiary.reminderType == 2) {
                // ✅ Start WorkManager
                scheduleExactNoteWorker(
                    context = context,
                    timeString = editorViewModel.timeText,
                    selectedDaysString = selectedDaysString,
                    uniqueWorkId = newDiary.id,
                    date = editorViewModel.dateText
                )
            } else {
                cancelScheduledExactNoteWorkerIfExists(context, newDiary.id)
            }
        }
    }

    BackHandler {
        // Only save if not saved already
        if (!hasSaved) {
            coroutineScope.launch {
                val selectedDaysString = editorViewModel.repeatableDays
                    .filter { it.isSelected }
                    .joinToString(",") { it.day.toString() }
                val images = Json.encodeToString(editorViewModel.selectedImages)
                val canvas = Json.encodeToString(editorViewModel.canvasItems)

                val newDiary = Diary(
                    title = editorViewModel.title.ifEmpty { "Untitled" },
                    body = richTextState.toHtml(),
                    isFavourite = editorViewModel.isPinned,
                    isLocked = editorViewModel.isLocked,
                    folderId = editorViewModel.selectedFolder?.id ?: "0",
                    backgroundId = editorViewModel.selectedBackground.id,
                    images = images,
                    stickers = canvas,
                    reminderTime = editorViewModel.timeText,
                    reminderDate = editorViewModel.dateText,
                    reminderType = editorViewModel.selectReminderType.id,
                    repeatDays = selectedDaysString,
                )
                diaryViewModel.upsertDiary(newDiary)
                hasSaved = true

                // Schedule or cancel reminders
                if (newDiary.reminderType == 2) {
                    scheduleExactNoteWorker(
                        context = context,
                        timeString = editorViewModel.timeText,
                        selectedDaysString = selectedDaysString,
                        uniqueWorkId = newDiary.id,
                        date = editorViewModel.dateText
                    )
                } else {
                    cancelScheduledExactNoteWorkerIfExists(context, newDiary.id)
                }
            }
        }
        navController.navigateUp()
    }

    val modeViewModel = ModeViewModel(context)
    val themeViewModel = ThemeViewModel(context)
    val isDarkMode by modeViewModel.isDarkTheme.collectAsState()
    val isTheme by themeViewModel.isTheme.collectAsState()
    val settingStore = SettingsStore(context)
    val isSystemFont by settingStore.getUseSystemFontKey.collectAsState(initial = false)

    val backgroundColor =
        if (editorViewModel.theme.cat == "solid") Color(editorViewModel.theme.headerColor.toColorInt()).copy(
            0.2f
        ) else Color(editorViewModel.theme.backgroundColor.toColorInt())

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
            containerColor = ColorWhite
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
                        "add_diary",
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
        }
    }
}

