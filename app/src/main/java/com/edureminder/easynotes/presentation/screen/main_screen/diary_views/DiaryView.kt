package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.room.diary.Diary
import com.edureminder.easynotes.room.diary.DiaryPreview
import com.edureminder.easynotes.room.diary.DiaryViewModel
import com.edureminder.easynotes.ui.Container
import com.edureminder.easynotes.ui.Primary

@Composable
fun DiaryView (navController: NavController) {
    val context = LocalContext.current
    val diaryViewModel: DiaryViewModel = hiltViewModel()
    val searchText = remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(true) }

    // Load filter preferences
    val diaryFilter = remember { DiaryFilterPreference(context) }

    val selectedFolder = remember { mutableStateOf(diaryFilter.folder) }
    val selectedMood = remember { mutableStateOf(diaryFilter.mood) }
    val sortOption = remember { mutableStateOf(diaryFilter.sortOption ?: "createdDesc") }
    val listState = rememberLazyListState()
    val diaryList by diaryViewModel.diaries

    // Load diaries with filters
    LaunchedEffect(searchText.value, selectedFolder.value, selectedMood.value, sortOption.value) {
        diaryViewModel.loadDiaries(
            search = searchText.value.ifEmpty { null },
            folderId = selectedFolder.value,
            mood = selectedMood.value,
            sortBy = sortOption.value
        )
    }

    // Save filters whenever they change
    LaunchedEffect(selectedFolder.value, selectedMood.value, sortOption.value) {
        diaryFilter.folder = selectedFolder.value
        diaryFilter.mood = selectedMood.value
        diaryFilter.sortOption = sortOption.value
    }
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 }
    }


    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .drawBehind {
                    if(isScrolled){
                        val shadowHeight = 12.dp.toPx() // adjust shadow thickness
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint()
                            paint.color = android.graphics.Color.BLACK
                            paint.alpha = (0.2f * 255).toInt()
                            paint.maskFilter = BlurMaskFilter(shadowHeight, BlurMaskFilter.Blur.NORMAL)
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
            Text(
                text = "Diary",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
            )
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .weight(1f)

            ) {
                if(isSearchOpen){
                    BasicTextField(
                        value = searchText.value,
                        onValueChange = {
                            if(it.length <= 30){
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
                            ){
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
                                    if(searchText.value.isEmpty()) {
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
                                        ){
                                            if(searchText.value.isNotEmpty()){
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
                    Row (
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(Color.Red)
                            .padding(horizontal = 5.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.crown_icon),
                            contentDescription = "crown icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(15.dp)
                        )
                        Text(
                            text = "PREMIUM",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = {}
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
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.option),
                            contentDescription = "search icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                }
            }

        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            item {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = MaterialTheme.shapes.medium
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.White),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.diary_background_image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    Box (
                        modifier = Modifier
                            .padding(12.dp)
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.paint_palette),
                            contentDescription = "paint palette icon",
                            tint = Primary
                        )
                    }
                }
            }
            items(diaryList){ diary ->
                DiaryItem(diary, navController)
            }
        }

    }
}
