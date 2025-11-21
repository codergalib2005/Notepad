package com.edureminder.notebook.presentation.screen.main_screen.task_views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UTurnLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.edureminder.notebook.datastore.SettingsStore
import com.edureminder.notebook.notification.cancelNotification
import com.edureminder.notebook.room.folder.Folder
import com.edureminder.notebook.room.todo.Todo
import com.edureminder.notebook.room.todo.TodoStatus
import com.edureminder.notebook.room.todo.TodoViewModel
import com.edureminder.notebook.ui.ColorBlack
import com.edureminder.notebook.ui.Primary
import com.edureminder.notebook.utils.convertTo12HourFormat
import com.edureminder.notebook.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun TodoItemView(
    todo: Todo,
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier,
    navController: NavController,
    currentlySwipedTodoId: MutableState<String>,
    onSnackbarUpdate: (String) -> Unit,
    is24HourClock: State<Boolean>,
    folderMap: Map<String, Folder>,
) {
    val swipeState = rememberSwipeableState(0)
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentlySwipedTodoId.value) {
        if (currentlySwipedTodoId.value != todo.id && swipeState.currentValue != 0) {
            scope.launch {
                swipeState.animateTo(0)
            }
        }
    }

    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue == 1) {
            currentlySwipedTodoId.value = todo.id
        } else if (currentlySwipedTodoId.value == todo.id) {
            currentlySwipedTodoId.value = ""
        }
    }

    var isSymbolSheetOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val settingsStore = SettingsStore(context = context)
    val savedSoundKey = settingsStore.getSoundKey.collectAsState(initial = true)

    val folderColor = folderMap[todo.folderId]?.color
        ?.toColorInt()
        ?.let { Color(it) }
        ?: Primary

    val swipeWidth = if(todo.status == TodoStatus.COMPLETED) 286f else 430f // Width in pixels to swipe (for 3 buttons)

    val anchors = mapOf(
        0f to 0,
        -swipeWidth to 1
    )

    Box(
        modifier = modifier
            .swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal,
                resistance = null,
                reverseDirection = false
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    MaterialTheme.shapes.large
                )
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
        ) {
            ActionButton(
                "Copy",
                Color(0xFFF6F6FA),
                contentColor = Color.Gray,
                Icons.Default.ContentCopy
            ) {
//                navController.navigate(Screen.CopyTaskScreen(todo.id))
            }
            if(todo.status != TodoStatus.COMPLETED){
                ActionButton(
                    modifier = Modifier
                        .rotate(
                            if(todo.status == TodoStatus.CANCELLED) 90f else 0f
                        ),
                    label = if(todo.status == TodoStatus.CANCELLED) "Undo" else "Skip",
                    bgColor = Color(0xFFF6F6FA),
                    contentColor = Color.Gray,
                    icon = if(todo.status == TodoStatus.CANCELLED) Icons.Default.UTurnLeft else Icons.Default.SkipNext,
                ) {
                    viewModel.togglePendingOrCancelled(
                        todoId = todo.id,
                        context = context
                    )
                }
            }
            ActionButton(
                "Delete",
                Color.Red,
                contentColor = Color.White,
                Icons.Default.Delete
            ) {
                viewModel.deleteTodo(todo) { deletedId ->
                    cancelNotification(context, todo)
                    onSnackbarUpdate("Todo deleted successfully")
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                .clip(
                    MaterialTheme.shapes.large
                )
                .background(Color.White)
//                .background(
//                    colorConverter(todo.color).copy(0.4f)
//                )
                .clickable {
//                    navController.navigate(
//                        Screen.EditTask(todo.id)
//                    )
                }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        viewModel.togglePendingOrCompleted(
                            todoId = todo.id,
                            context = context
                        )
                        if (todo.status == TodoStatus.PENDING) {
                            viewModel.isAnimationPlayingState.value = true
                            if (savedSoundKey.value == true) {
                                viewModel.playCompletedSound(context)
                            }
                        } else {
                            viewModel.isAnimationPlayingState.value = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {

                if (todo.status == TodoStatus.COMPLETED) {
                    Icon(
                        painter = painterResource(id = R.drawable.check_circle),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier
                            .size(25.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.circle),
                        contentDescription = null,
                        tint = ColorBlack.copy(0.8f),
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = todo.title,
                        fontSize = 14.sp,
                        color = Color.Black.copy(0.8f),
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        lineHeight = 18.sp,
                        textDecoration = if (todo.status == TodoStatus.COMPLETED) TextDecoration.LineThrough else TextDecoration.None
                    )

                    Row(
                        modifier = Modifier.padding(top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
//                        tag?.let {
//                            Box(
//                                modifier = Modifier
//                                    .clip(MaterialTheme.shapes.extraSmall)
//                                    .background(Color.White)
//                                    .padding(horizontal = 3.dp, vertical = 1.dp),
//                            ) {
//                                Text(
//                                    text = it.title,
//                                    fontSize = 12.sp,
//                                    color = colorConverter(todo.color!!)
//                                )
//                            }
//                        }
                        if (todo.time.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(Color.White)
                                    .padding(horizontal = 2.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(14.dp),
                                )
                                Text(
                                    text = convertTo12HourFormat(todo.time, is24HourClock.value),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
            Column {
                if(todo.flagId == 0){
                    IconButton(
                        onClick = {
                            isSymbolSheetOpen = !isSymbolSheetOpen
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.task_symbol_clear),
                            contentDescription = null,
                            tint = ColorBlack.copy(0.8f),
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            isSymbolSheetOpen = !isSymbolSheetOpen
                        }
                    ) {
                        Symbols.symbols.find { it.id == todo.flagId }?.icon()
                    }
                }
                MarkWithSymbol(
                    isSymbolSheetOpen,
                    onUpdateSymbol = { flagId ->
                        viewModel.updateFlag(todo.id, flagId)
                        isSymbolSheetOpen = false
                    },
                    onDismissSymbolSheet = {
                        isSymbolSheetOpen = false
                    },
                    onClearSymbol = {
                        viewModel.clearFlag(todo.id)
                        isSymbolSheetOpen = false
                    }
                )
            }
        }
    }
}




@Composable
fun ActionButton(
    label: String,
    bgColor: Color,
    contentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(62.dp)
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.large)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = contentColor,
            modifier = modifier
        )
        Text(label, fontSize = 12.sp, color = contentColor)
    }
}