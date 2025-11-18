package com.edureminder.easynotes.presentation.screen.diary_screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.DialogDefaults
import com.edureminder.easynotes.R
import com.edureminder.easynotes.datastore.SettingsStore
import com.edureminder.easynotes.presentation.screen.diary_screen.components.EditDiaryContent
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.TextStyleSheet
import com.edureminder.easynotes.presentation.screen.edit_note.components.AddNotesContent
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.room.note.Note
import com.edureminder.easynotes.room.note.NoteViewModel
import com.edureminder.easynotes.room.note.Type
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.NotepadTheme
import com.edureminder.easynotes.ui.mode.ModeViewModel
import com.edureminder.easynotes.ui.theme.ThemeViewModel
import com.edureminder.easynotes.work.note.cancelScheduledExactNoteWorkerIfExists
import com.edureminder.easynotes.work.note.scheduleExactNoteWorker
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.launch
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
        if (folders.isNotEmpty()) {
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
            navController.navigateUp()

            if (newNote.reminderType == 2) {
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
                    EditDiaryContent(
                        sharedTransitionScope,
                        animatedContentScope,
                        richTextState,
                        navController,
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
                            androidx.compose.material3.Text(
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
}

