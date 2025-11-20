package com.feature.edureminder.texteditor.model

import androidx.compose.runtime.Composable
import com.feature.edureminder.texteditor.annotation.ExperimentalRichTextApi

@ExperimentalRichTextApi
public object DefaultImageLoader: ImageLoader {

    @Composable
    override fun load(model: Any): ImageData? = null

}