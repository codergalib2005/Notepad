package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.R
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.presentation.screen.diary_screen.components.SmallThumbnailImage
import com.edureminder.easynotes.room.diary.Diary
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DiaryItem(diary: Diary) {
    val restoredImages = Json.decodeFromString<List<String>>(diary.images)

    Row (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        /**
         * Date Section
         */
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .width(60.dp)
                .drawBehind {
                    val strokeWidthPx = 5f

                    // X position for the right-side line
                    val x = size.width - strokeWidthPx / 2

                    drawLine(
                        color = Color.White,
                        start = Offset(x, 0f),              // Top of the right edge
                        end = Offset(x, size.height),       // Bottom of the right edge
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                }
                .padding(top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "17",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(0.9f)
            )
            Text(
                text = "Oct",
                fontSize = 18.sp,
                color = Color.Black.copy(0.7f)
            )
            Text(
                text = "2025",
                fontSize = 18.sp,
                color = Color.Black.copy(0.7f)
            )
        }
        /**
         * Content Section
         */
        Column (
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "8:34 PM",
                fontSize = 14.sp,
                color = Color.Black.copy(0.7f),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(Color.White)
                    .padding(vertical = 2.dp, horizontal = 5.dp)
            )
            Text(
                text = diary.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = diary.body,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                fontSize = 17.sp,
                color = Color.Black.copy(0.7f)
            )
            LazyRow(
                modifier = Modifier
                    .padding(top = 10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(restoredImages) { image ->
                    SmallThumbnailImage(
                        image
                    )
                }
            }
        }
    }
}