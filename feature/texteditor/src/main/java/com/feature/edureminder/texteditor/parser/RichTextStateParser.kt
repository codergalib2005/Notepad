package com.feature.edureminder.texteditor.parser

import com.feature.edureminder.texteditor.model.RichTextState


internal interface RichTextStateParser<T> {

    fun encode(input: T): RichTextState

    fun decode(richTextState: RichTextState): T

}