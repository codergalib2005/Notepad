package com.edureminder.easynotes.presentation.screen.main_screen.task_add

import android.graphics.drawable.shapes.Shape
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.SelectorProperties
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.edureminder.easynotes.R
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.presentation.screen.edit_note.SelectedDay
import com.edureminder.easynotes.presentation.screen.edit_note.components.FolderList
import com.edureminder.easynotes.room.folder.FolderViewModel
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale


data class SubTask(
    val id: Long = System.currentTimeMillis(),
    var text: String = "",
    var isCompleted: Boolean = false
)
data class SelectedDay(
    val day: Int,
    val isSelected: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAddSheet(
    bottomSheet: SheetState,
    navController: NavController,
) {
    val folderViewModel: FolderViewModel = hiltViewModel()
    val editorViewModel: NoteEditorViewModel = hiltViewModel()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isDatePicker by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var amPmState by remember { mutableStateOf<TimeFormat?>(null) }
    val repeatableDays = remember {
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
    val defaultDateTime = LocalDateTime.now()
        .plusDays(1)
        .withSecond(0)
        .withNano(0)
        .plusMinutes(5)

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
    var subTasks by remember { mutableStateOf(emptyList<SubTask>()) }


    if(editorViewModel.addTaskSheetOpen){
        ModalBottomSheet(
            sheetState = bottomSheet,
            onDismissRequest = {
                editorViewModel.addTaskSheetOpen = false
            },
            dragHandle = null,
            containerColor = ColorWhite,
            shape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp),

        ) {
            Column {

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorWhite)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 13.dp)
                            .padding(top = 5.dp)
                    ){
                        Text(
                            text = "Add Task",
                            color = ColorBlack.copy(alpha = 0.9f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                editorViewModel.addTaskSheetOpen = false
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Primary.copy(0.2f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                    }
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
                            .padding(start = 10.dp, end = 10.dp)
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
                        .heightIn(max = 150.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
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
                                if(newText.length <= 300) {
                                    subTasks = subTasks.map {
                                        if (it.id == subtask.id) it.copy(text = newText)
                                        else it
                                    }.toMutableList()
                                }
                            }
                        )
                    }

                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                ){
                    AnimatedVisibility(isDatePicker) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            WheelDateTimePicker(
                                startDateTime = selectedDateTime ?: defaultDateTime,
                                minDateTime = LocalDateTime.now(),
                                timeFormat = amPmState ?: TimeFormat.HOUR_24, // use state if selected
                                textStyle = MaterialTheme.typography.titleSmall,
                                textColor = ColorBlack,
                                selectorProperties = WheelPickerDefaults.selectorProperties(
                                    shape = CircleShape,
                                    color = Primary.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp,
                                        Primary
                                    )
                                )
                            ) { snappedDateTime ->
                                selectedDateTime = snappedDateTime
                            }
                            Column (
                                modifier = Modifier
                                    .height(100.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                repeatableDays.value.forEachIndexed { index,  day ->
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(CircleShape)
                                            .background(Primary.copy(0.1f))
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() }
                                            ){
                                                repeatableDays.value = repeatableDays.value.toMutableList().also {
                                                    it[index] = it[index].copy(isSelected = !it[index].isSelected)
                                                }
                                            }
                                            .padding(vertical = 5.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (day.isSelected) R.drawable.check_circle else R.drawable.circle
                                            ),
                                            contentDescription = null,
                                            tint = if (day.isSelected) Primary else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = SimpleDateFormat("EEE", Locale.ENGLISH).format(
                                                Calendar.getInstance().apply {
                                                    set(Calendar.DAY_OF_WEEK, day.day)
                                                }.time
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                                            editorViewModel.isOpenFolderList =
                                                !editorViewModel.isOpenFolderList
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
                            IconButton(
                                onClick = {
                                    isDatePicker = !isDatePicker
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
                                    if (subTasks.size < 100) {
                                        subTasks = (subTasks + SubTask()).toMutableList()
                                    }
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
                                .background(
                                    if(title.isNotEmpty()) Primary else Color.LightGray
                                ),
                            enabled = title.isNotEmpty(),
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
            .padding(start = 7.dp, end = 2.dp),
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
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = if (subtask.isCompleted) Color.Gray else ColorBlack,
                textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None

            ),
            decorationBox = { innerTextField ->
                if(subtask.text.isEmpty()) {
                    Text(
                        text = "Input the sub-task...",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None

                    )
                } else {
                    innerTextField()
                }
            },
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
