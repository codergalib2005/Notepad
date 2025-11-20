package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import android.graphics.BlurMaskFilter
import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.room.diary.DiaryPreview
import com.edureminder.easynotes.room.diary.DiaryViewModel
import com.edureminder.easynotes.ui.Container
import com.edureminder.easynotes.ui.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryView (navController: NavController) {

    val context = LocalContext.current
    val diaryViewModel: DiaryViewModel = hiltViewModel()

    // Shared preferences
    val diaryFilter = remember { DiaryFilterPreference(context) }

    // UI-only state
    var searchText by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf<String?>(null) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var isFilterSheetOpen by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )


    // Saved filters
    var selectedMood by remember { mutableStateOf(diaryFilter.mood) }
    var sortOption by remember { mutableStateOf(diaryFilter.sortOption ?: "createdDesc") }
    var createdAfter by remember { mutableStateOf(diaryFilter.createdAfter) }
    var createdBefore by remember { mutableStateOf(diaryFilter.createdBefore) }
    var updatedAfter by remember { mutableStateOf(diaryFilter.updatedAfter) }
    var updatedBefore by remember { mutableStateOf(diaryFilter.updatedBefore) }

    // ✔ READ FROM VIEWMODEL
    val diaries by diaryViewModel.diaries.collectAsState()
    val isLoading by diaryViewModel.isLoading.collectAsState()

    // Load diaries whenever filters change
    LaunchedEffect(
        searchText, selectedFolder, sortOption,
        createdAfter, createdBefore, updatedAfter, updatedBefore
    ) {
        diaryViewModel.loadDiaries(
            search = searchText.ifEmpty { null },
            folderId = selectedFolder,
            sortBy = sortOption,
            createdAfter = createdAfter,
            createdBefore = createdBefore,
            updatedAfter = updatedAfter,
            updatedBefore = updatedBefore
        )
    }




    // Save filters to SharedPreferences
    LaunchedEffect(selectedMood, sortOption, createdAfter, createdBefore, updatedAfter, updatedBefore) {
        diaryFilter.mood = selectedMood
        diaryFilter.sortOption = sortOption
        diaryFilter.createdAfter = createdAfter
        diaryFilter.createdBefore = createdBefore
        diaryFilter.updatedAfter = updatedAfter
        diaryFilter.updatedBefore = updatedBefore
    }

    val listState = rememberLazyListState()


    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 }
    }
    Log.d("Log1", "diaries -> $diaries")


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
                        value = searchText,
                        onValueChange = {
                            if(it.length <= 30){
                                searchText = it
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
                                    if(searchText.isEmpty()) {
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
                                            if(searchText.isNotEmpty()){
                                                searchText = ""
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
                            isFilterSheetOpen = true
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
            items(diaries){ diary ->
                DiaryItem(diary, navController)
            }
        }

    }
    FilterBottomSheet(
        sheetState = sheetState,
        diaryFilter = diaryFilter,
        isFilterSheetOpen = isFilterSheetOpen,
        onDismiss = { isFilterSheetOpen = false }
    )


}
