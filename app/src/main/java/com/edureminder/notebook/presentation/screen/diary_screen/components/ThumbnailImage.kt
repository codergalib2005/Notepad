package com.edureminder.notebook.presentation.screen.diary_screen.components

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
fun ThumbnailImage(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    filePath: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val file = File(context.filesDir, "images/$filePath")

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(file)
            .size(200, 200)
            .build()
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
            .clickable {
                onClick()
            }
    ) {
        with(sharedTransitionScope) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = filePath),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .matchParentSize()
            )
        }

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