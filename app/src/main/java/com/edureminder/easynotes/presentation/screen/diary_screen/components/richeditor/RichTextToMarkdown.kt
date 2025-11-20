package com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    ) {
        OutlinedRichTextEditor(
            modifier = Modifier
                .fillMaxWidth()
                .clickable (
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    editorViewModel.isEditable = true
                }
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