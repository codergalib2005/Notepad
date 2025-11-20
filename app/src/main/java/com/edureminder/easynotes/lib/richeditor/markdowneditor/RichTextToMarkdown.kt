package com.edureminder.easynotes.lib.richeditor.markdowneditor

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.edureminder.easynotes.data.Theme
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.viewmodels.MainViewModel
import com.feature.edureminder.texteditor.model.RichTextState
import com.feature.edureminder.texteditor.ui.material3.OutlinedRichTextEditor
import com.feature.edureminder.texteditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichTextToMarkdown(
    richTextState: RichTextState,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    theme: Theme,
    editorViewModel: NoteEditorViewModel,
    backgroundColor: Color,
    navController: NavController,
    mainViewModel: MainViewModel
) {

    val lineHeight = 24.sp
    val headerColor = Color(editorViewModel.theme.headerColor.toColorInt())


    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        OutlinedRichTextEditor(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(
                    if (theme.showTopLine) Modifier.linedBackground(lineHeight)
                    else Modifier
                )
                .focusRequester(focusRequester),
            state = richTextState,
            colors = RichTextEditorDefaults.outlinedRichTextEditorColors(
                textColor = Color("#000000".toColorInt()),
                cursorColor = Primary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                containerColor = Color.Transparent,
                disabledTextColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            ),
            contentPadding = PaddingValues(horizontal = 14.dp),
            readOnly = !editorViewModel.isEditable,
            maxLength = 15000,
            placeholder = {
                Text(
                    text = "Start typing here...",
                    color = Color.Black.copy(0.4f),
                    fontSize = 16.sp
                )
            },
        )
    }
}
fun Modifier.linedBackground(
    lineHeight: TextUnit,
    color: Color = Color.White,
    verticalOffset: Float = 4f // Shift lines down to better align with baseline
): Modifier = drawBehind {
    val lineHeightPx = lineHeight.toPx()
    var y = verticalOffset
    while (y < size.height) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += lineHeightPx
    }
}