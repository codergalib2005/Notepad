package com.edureminder.notebook.presentation.screen.main_screen.diary_views

import android.text.Html
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edureminder.notebook.presentation.navigation.Screen
import com.edureminder.notebook.presentation.screen.diary_screen.components.SmallThumbnailImage
import com.edureminder.notebook.room.diary.DiaryPreview
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DiaryItem(
    diary: DiaryPreview,
    navController: NavController
) {
    val restoredImages = Json.decodeFromString<List<String>>(diary.images)


    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){
                navController.navigate(Screen.EditDiaryScreen(diary.id, encrypted = true))
            },
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        /**
         * Date Section
         */
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .width(50.dp)
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
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = diary.createdAtDay.toString(),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(0.9f)
            )
            Text(
                text = diary.createdAtMonth,
                fontSize = 18.sp,
                color = Color.Black.copy(0.7f)
            )
            Text(
                text = diary.createdAtYear.toString(),
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
                text = diary.createdAtTime,
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
                text = if (diary.preview.isEmpty()) {
                        "Empty Body"
                } else {
                    // Clean the HTML tags and get the plain text
                    val plainText =
                        Html.fromHtml(diary.preview, Html.FROM_HTML_MODE_LEGACY).toString()
                            .trim()
                    // Limit to 100 characters
                    plainText.take(100).takeIf { it.isNotEmpty() } ?: "Empty Body"
                },
                fontSize = 17.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black.copy(0.7f)
            )
            Row(
                modifier = Modifier
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                restoredImages.take(5).forEach { image ->
                    SmallThumbnailImage(
                        image
                    )
                }
            }
        }
    }
}