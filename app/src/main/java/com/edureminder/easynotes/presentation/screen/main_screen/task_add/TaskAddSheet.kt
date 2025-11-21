package com.edureminder.easynotes.presentation.screen.main_screen.task_add

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.components.FolderList
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary


data class SubTask(
    val id: Long = System.currentTimeMillis(),
    var text: String = "",
    var isCompleted: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAddSheet(bottomSheet: SheetState, navController: NavController) {
    val folderViewModel: FolderViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = hiltViewModel()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(bottomSheet.currentValue) {
        if (bottomSheet.isVisible) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(folders) {
        if(folders.isNotEmpty()){
            editorViewModel.selectedFolder = folders.first()
        }
    }
    var title by remember { mutableStateOf("") }
    var subTasks by remember { mutableStateOf(mutableListOf<SubTask>()) }

    Log.d("Log1,", subTasks.map { it.id }.toString())

    LaunchedEffect(key1 = subTasks) {
        subTasks = mutableListOf(
            SubTask(1763719518360, text = "Task 1", isCompleted = true),
            SubTask(1763719518876, text = "Task 2", isCompleted = true),
            SubTask(1763719519392, text = "Task 3", isCompleted = true),
            SubTask(1763719519908, text = "Task 4", isCompleted = false),
            SubTask(1763719520424, text = "Task 5", isCompleted = true),
            SubTask(1763719520940, text = "Task 6", isCompleted = true),
        )
    }

    ModalBottomSheet(
        sheetState = bottomSheet,
        onDismissRequest = {},
        dragHandle = null,
        containerColor = ColorWhite,
        shape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp)
    ) {
        Column {

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorWhite)
            ) {
                BasicTextField(
                    value = title,
                    onValueChange = {
                        if(it.length <= 500) {
                            title = it
                        }
                    },
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp)
                        ) {
                            if(title.isEmpty()) {
                                Text(
                                    text = "Input task title...",
                                    color = ColorBlack.copy(alpha = 0.5f),
                                    fontSize = 18.sp,
                                )
                            } else {
                                innerTextField()
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 15.dp, start = 15.dp, end = 15.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.LightGray.copy(0.2f))
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = ColorBlack.copy(alpha = 0.9f),
                    ),
                    cursorBrush = SolidColor(Primary),
                    maxLines = 1,
                    singleLine = true
                )
            }
            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(subTasks) { subtask ->
                    SubTaskView(
                        subtask,
                        onCheckedChange = { checked ->
                            subTasks = subTasks.map {
                                if (it.id == subtask.id) it.copy(isCompleted = checked)
                                else it
                            }.toMutableList()
                        },
                        onDelete = {
                            subTasks = subTasks
                                .filter { it.id != subtask.id }
                                .toMutableList()
                        },
                        onValueChange = { newText ->
                            subTasks = subTasks.map {
                                if (it.id == subtask.id) it.copy(text = newText)
                                else it
                            }.toMutableList()
                        }
                    )
                }

            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
               Row(
                   verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier.weight(1f)
               ) {
                   editorViewModel.selectedFolder?.let {
                       Row(
                           modifier = Modifier
                               .clip(CircleShape)
                               .background(Color.LightGray.copy(0.2f))
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
                               text = it.name.take(20),
                               color = ColorBlack.copy(alpha = 0.8f),
                               fontSize = 15.sp,
                               modifier = Modifier.padding(start = 5.dp)
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
                   IconButton(
                       onClick = {

                       }
                   ) {
                       Icon(
                           painter = painterResource(id = R.drawable.calendar),
                           contentDescription = null,
                           tint = Primary,
                           modifier = Modifier.size(22.dp)
                       )
                   }
                   IconButton(
                       onClick = {
                           subTasks = (subTasks + SubTask()).toMutableList()
                       }
                   ) {
                       Icon(
                           painter = painterResource(id = R.drawable.subttasks),
                           contentDescription = null,
                           tint = Primary,
                           modifier = Modifier.size(22.dp)
                       )
                   }
               }
                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Primary)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.up_arrow),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SubTaskView(
    subtask: SubTask,
    onCheckedChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 2.dp, start = 7.dp, end = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ✔ CHECKBOX
        IconButton(
            onClick = { onCheckedChange(!subtask.isCompleted) },
            modifier = Modifier.size(26.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (subtask.isCompleted) R.drawable.check_circle else R.drawable.circle
                ),
                contentDescription = null,
                tint = if (subtask.isCompleted) Primary else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }

        // ✏ TEXT FIELD
        BasicTextField(
            value = subtask.text,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = if (subtask.isCompleted) Color.Gray else ColorBlack,
                textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None

            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
        )

        // ❌ DELETE BUTTON
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Delete",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
