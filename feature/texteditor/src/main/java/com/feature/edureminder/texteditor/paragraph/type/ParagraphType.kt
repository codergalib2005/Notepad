package com.feature.edureminder.texteditor.paragraph.type

import androidx.compose.ui.text.ParagraphStyle
import com.feature.edureminder.texteditor.model.RichSpan
import com.feature.edureminder.texteditor.model.RichTextConfig

internal interface ParagraphType {

    fun getStyle(config: RichTextConfig): ParagraphStyle

    val startRichSpan: RichSpan

    fun getNextParagraphType(): ParagraphType

    fun copy(): ParagraphType

    companion object {
        val ParagraphType.startText : String get() = startRichSpan.text
    }
}