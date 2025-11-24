package com.edureminder.notebook.presentation.screen.main_screen.task_views

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.edureminder.notebook.R
import com.edureminder.notebook.datastore.SettingsStore
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.screen.main_screen.note_views.FolderItem
import com.edureminder.notebook.room.folder.FolderViewModel
import com.edureminder.notebook.room.note.Note
import com.edureminder.notebook.room.todo.Todo
import com.edureminder.notebook.room.todo.TodoViewModel
import com.edureminder.notebook.ui.ColorBlack
import com.edureminder.notebook.ui.ColorWhite
import com.edureminder.notebook.ui.Container
import com.edureminder.notebook.ui.Primary
import kotlinx.coroutines.delay

@Composable
fun TaskView (
    navController: NavHostController,
    onSnackbarUpdate: (String) -> Unit,
    onToggleSidebar: () -> Unit
){
    val context = LocalContext.current
    val todoViewModel: TodoViewModel = hiltViewModel()
    val folderViewModel: FolderViewModel = hiltViewModel()
    val folders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    var todoListFromFlow by remember { mutableStateOf(emptyList<Todo>()) }

    val listState = rememberLazyListState()
    var selectedFolderId by rememberSaveable { mutableStateOf("0") }

    val isScrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 } }
    val selectedNotes = remember { mutableStateOf(emptyList<Note>()) }
    val searchBarVisible = rememberSaveable { mutableStateOf(false) }
    var isSearchOpen by remember { mutableStateOf(false) }
    val searchText = rememberSaveable { mutableStateOf("") }
    var isSettingOpened by remember { mutableStateOf(false) }
    val isMoreFeatureOpened = remember { mutableStateOf(false) }
    val currentlySwipedTodoId = remember { mutableStateOf("") }
    val settingsStore = SettingsStore(context)
    val savedAnimationKey = settingsStore.getAnimationKey.collectAsState(initial = true)
    val is24HourClock = settingsStore.getClockKey.collectAsState(initial = false)

    LaunchedEffect(key1 = Unit) {
        todoViewModel.getTodosByDate().collect { todos ->
            todoListFromFlow = todos
        }
    }
    val folderMap = folders.associateBy { it.id }
//    val loadedList = Json.decodeFromString<List<SubTask>>(jsonString)


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (selectedNotes.value.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .drawBehind {
                        if (isScrolled) {
                            val shadowHeight = 12.dp.toPx() // adjust shadow thickness
                            drawIntoCanvas { canvas ->
                                val paint = Paint().asFrameworkPaint()
                                paint.color = android.graphics.Color.BLACK
                                paint.alpha = (0.2f * 255).toInt()
                                paint.maskFilter =
                                    BlurMaskFilter(shadowHeight, BlurMaskFilter.Blur.NORMAL)
                                canvas.nativeCanvas.drawRect(
                                    0f,
                                    size.height - shadowHeight,
                                    size.width,
                                    size.height,
                                    paint
                                )
                            }
                        }
                    }
                    .background(Container),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onToggleSidebar()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .size(28.dp)
                        )
                    }
                    if (!searchBarVisible.value) {
                        Text(
                            text = "Notepad",
                            fontSize = 20.sp,
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(1f)

                ) {
                    if (isSearchOpen) {
                        BasicTextField(
                            value = searchText.value,
                            onValueChange = {
                                if (it.length <= 30) {
                                    searchText.value = it
                                }
                            },
                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(0.4f)),
                            decorationBox = { innerTextField ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 13.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.search),
                                            contentDescription = "search icon",
                                            tint = Color.Gray,
                                            modifier = Modifier
                                                .size(20.dp)
                                        )
                                        if (searchText.value.isEmpty()) {
                                            Text(
                                                text = "Search...",
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        } else {
                                            innerTextField()
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "close icon",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() },
                                            ) {
                                                if (searchText.value.isNotEmpty()) {
                                                    searchText.value = ""
                                                } else {
                                                    isSearchOpen = false
                                                }
                                            }
                                    )

                                }
                            }
                        )
                    } else {
//                    Row (
//                        modifier = Modifier
//                            .padding(end = 10.dp)
//                            .clip(MaterialTheme.shapes.extraSmall)
//                            .background(Color.Red)
//                            .padding(horizontal = 5.dp, vertical = 3.dp),
//                        horizontalArrangement = Arrangement.spacedBy(3.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.crown_icon),
//                            contentDescription = "crown icon",
//                            tint = Color.White,
//                            modifier = Modifier
//                                .size(15.dp)
//                        )
//                        Text(
//                            text = "PREMIUM",
//                            fontSize = 10.sp,
//                            color = Color.White,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
                        IconButton(
                            onClick = {
                                isSearchOpen = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "search icon",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                isSettingOpened = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.option),
                                contentDescription = "search icon",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                        Column {
                            DropdownMenu(
                                expanded = isMoreFeatureOpened.value,
                                onDismissRequest = { isMoreFeatureOpened.value = false },
                                modifier = Modifier
                                    .width(170.dp),
                                containerColor = ColorWhite,
                                shadowElevation = 2.dp,
                            ) {
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        isMoreFeatureOpened.value = false
                                        navController.navigate(Screen.AuthScreen)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            contentDescription = "Locked"
                                        },
                                    text = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked icon",
                                                tint = ColorBlack.copy(0.8f),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = "Locked",
                                                color = ColorBlack.copy(0.8f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                )
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        navController.navigate(Screen.RecycleNoteScreen)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            contentDescription = "Recycle bin"
                                        },
                                    text = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Recycling,
                                                contentDescription = "Recycle bin icon",
                                                tint = ColorBlack.copy(0.8f),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = "Recycle bin",
                                                color = ColorBlack.copy(0.8f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                )
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(0.dp),
                                    onClick = {
                                        navController.navigate(Screen.SettingScreen)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics {
                                            contentDescription = "Setting"
                                        },
                                    text = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Setting icon",
                                                tint = ColorBlack.copy(0.8f),
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = "Setting",
                                                color = ColorBlack.copy(0.8f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                )
                            }
                        }
                        IconButton(onClick = {
                            isMoreFeatureOpened.value = !isMoreFeatureOpened.value

                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Primary)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        selectedNotes.value = emptyList()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "${selectedNotes.value.size} - Selected",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedNotes.value.size == 1) {

                        IconButton(
                            onClick = {
                                val selectedNote = selectedNotes.value.first()
                                if (selectedNote.isFavourite) {
//                                    notesViewModel.unpinNote(selectedNote.id)
                                } else {
//                                    notesViewModel.pinNote(selectedNote.id)
                                }
                                selectedNotes.value = emptyList()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .rotate(35f)
                            )
                        }
                    }
                }
            }
        }
        Column {
            AnimatedVisibility(visible = selectedNotes.value.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = CircleShape,
                                    )
                                    .background(if (selectedFolderId == "0") Primary else ColorWhite)
                                    .clickable(
                                        enabled = selectedFolderId != "0"
                                    ) {
                                        selectedFolderId = "0"
                                    }
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 5.dp
                                    )
                            ) {
                                Text(
                                    text = "All",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFolderId == "0") Color.White else ColorBlack.copy(
                                        0.6f
                                    )
                                )
                            }
                        }
                        items(folders) { folder ->
                            FolderItem(
                                folder,
                                selectedFolderId,
                                onChange = {
                                    selectedFolderId = folder.id
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.padding(start = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = CircleShape,
                                )
                                .background(Color.White)
                                .clickable() {
                                    navController.navigate(Screen.FolderScreen)
                                }
                                .padding(
                                    horizontal = 15.dp,
                                    vertical = 5.dp
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = ColorBlack.copy(0.7f),
                                modifier = Modifier
                                    .size(19.dp)
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                bottom = 98.dp,
                top = 10.dp
            )
        ) {
            itemsIndexed(todoListFromFlow) { index, item ->
                var visible by remember(Unit) { mutableStateOf(false) }

                // Scale animation (already in your code)
                val animatedScale by animateFloatAsState(
                    targetValue = if (visible) 1f else 0.95f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

                // Alpha (fade-in) animation
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )

                // Translation Y (slide-up effect)
                val animatedTranslationY by animateDpAsState(
                    targetValue = if (visible) 0.dp else 20.dp,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )

                // Optional: subtle rotation to make it more dynamic
                val animatedRotation by animateFloatAsState(
                    targetValue = if (visible) 0f else 5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                )

                LaunchedEffect(Unit) {
                    visible = false
                    delay(index * 30L) // staggered delay
                    visible = true
                }

                TodoItemView(
                    todo = item,
                    viewModel = todoViewModel,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .height(85.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                            alpha = animatedAlpha
                            translationY = animatedTranslationY.toPx()
                            rotationZ = animatedRotation
                        },
                    navController,
                    currentlySwipedTodoId = currentlySwipedTodoId,
                    onSnackbarUpdate,
                    is24HourClock,
                    folderMap
                )
            }
        }
    }
}