package com.edureminder.easynotes.presentation.screen.main_screen.diary_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.R
import com.edureminder.easynotes.ui.Primary

@Composable
fun DiaryView () {
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
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            item {
                Box (
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
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
            item {
                Row (
                    modifier = Modifier
                        .drawBehind {
                            val strokeWidthPx = 5f
                            val x = size.width - strokeWidthPx / 2
                            drawLine(
                                color = Color.White, start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = strokeWidthPx,
                                cap = StrokeCap.Round
                            )
                        }
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
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
                            text = "ফৌজদারী কার্যবিধি ৩৮২ ধারা অনুযায়ী স্ত্রীলোক মৃ'ত্যুদ'ণ্ড স্থগিত করতে পারে যদি সে গর্ভবতী থাকে।".slice(0..65),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "ফৌজদারী কার্যবিধি \uD83D\uDE01\uD83D\uDE01\n ৩৮২ ধারা \uD83D\uDE01\uD83D\uDE01\n অনুযায়ী স্ত্রীলোক \uD83D\uDE01\uD83D\uDE01\n মৃ'ত্যুদ'ণ্ড স্থগিত করতে পারে যদি সে গর্ভবতী থাকে।",
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 17.sp,
                            color = Color.Black.copy(0.7f)
                        )
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.diary_background_image),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(MaterialTheme.shapes.extraSmall),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun DiaryPreview () {
    DiaryView()
}