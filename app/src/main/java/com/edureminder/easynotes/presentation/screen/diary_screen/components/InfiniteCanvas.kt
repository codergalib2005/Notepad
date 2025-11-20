package com.edureminder.easynotes.presentation.screen.diary_screen.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel

@Composable
fun InfiniteCanvas(
    editorViewModel: NoteEditorViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)  // 🔥 Canvas gets tap priority
            .pointerInput(Unit) {
                detectTapGestures {
                    editorViewModel.deselectAll()
                }
            }
    ) {
        editorViewModel.canvasItems.forEach { item ->
            CanvasItem(
                item = item,
                onUpdate = { editorViewModel.updateItem(it) },
                onDelete = { editorViewModel.deleteItem(it) },
                onSelect = { editorViewModel.selectItem(it) }
            )
        }
    }
}
