package com.edureminder.easynotes.presentation.screen.diary_screen.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SmallThumbnailImage(
    filePath: String,
) {
    val context = LocalContext.current

    val file = File(context.filesDir, "images/$filePath")

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(file)
            .size(100, 100)
            .build()
    )

    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Icon(
                    imageVector = Icons.Default.ImageSearch,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
            }
            is AsyncImagePainter.State.Error -> {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    tint = Color.Gray
                )
            }
            else -> {}
        }
    }
}