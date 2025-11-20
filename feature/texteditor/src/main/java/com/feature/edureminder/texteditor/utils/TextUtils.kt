package com.feature.edureminder.texteditor.utils

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.feature.edureminder.texteditor.annotation.ExperimentalRichTextApi
import com.feature.edureminder.texteditor.model.RichSpan
import com.feature.edureminder.texteditor.paragraph.RichParagraph
import com.feature.edureminder.texteditor.paragraph.type.ParagraphType.Companion.startText
import kotlin.text.buildString
import kotlin.text.appendLine
import kotlin.text.StringBuilder

@OptIn(ExperimentalRichTextApi::class)
internal fun toText(richParagraphList: List<RichParagraph>): String =
    buildString {
        richParagraphList.fastForEachIndexed { i, richParagraphStyle ->
            append(richParagraphStyle.type.startText)

            appendRichSpanList(
                richSpanList = richParagraphStyle.children,
            )

            if (i != richParagraphList.lastIndex)
                appendLine()
        }
    }

@OptIn(ExperimentalRichTextApi::class)
internal fun StringBuilder.appendRichSpanList(
    richSpanList: List<RichSpan>,
) {
    richSpanList.fastForEach { richSpanStyle ->
        append(richSpanStyle.text)

        if (richSpanStyle.children.isNotEmpty())
            appendRichSpanList(
                richSpanList = richSpanStyle.children,
            )
    }
}