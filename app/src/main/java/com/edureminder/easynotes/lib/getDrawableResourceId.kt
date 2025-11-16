package com.edureminder.easynotes.lib

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun getDrawableResourceId(context: Context, image: String): AsyncImagePainter {
    val resources: Resources = context.resources
    val drawableResourceId = resources.getIdentifier("$image", "drawable", context.packageName)
    return rememberAsyncImagePainter(model = drawableResourceId)
}
@Composable
fun getDrawableResourcePainter(context: Context, image: String): Painter {
    val drawableResourceId = context.resources.getIdentifier(image, "drawable", context.packageName)
    return painterResource(id = drawableResourceId)
}