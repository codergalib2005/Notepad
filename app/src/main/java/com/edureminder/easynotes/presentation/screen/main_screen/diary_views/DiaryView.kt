package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.room.diary.Diary
import com.edureminder.easynotes.room.diary.DiaryViewModel
import com.edureminder.easynotes.ui.Primary

@Composable
fun DiaryView (navController: NavController) {
    val diaryViewModel: DiaryViewModel = hiltViewModel()
    // State for diaries
    var diaries by remember { mutableStateOf<List<Diary>>(emptyList()) }
    // State for loading
    var isLoading by remember { mutableStateOf(true) }

    // Load diaries once when the composable enters composition
    LaunchedEffect(Unit) {
        diaryViewModel.getAllDiaries { fetchedDiaries ->
            diaries = fetchedDiaries
            isLoading = false
        }
    }


    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Diary",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
            Row (
                modifier = Modifier
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
        }

        LazyColumn(
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
}
