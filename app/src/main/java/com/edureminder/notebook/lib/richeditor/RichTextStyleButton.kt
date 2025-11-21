package com.edureminder.notebook.lib.richeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.edureminder.notebook.ui.Primary

@Composable
fun RichTextStyleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    tint: Color? = null,
    isSelected: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                if (isSelected) {
                    Primary.copy(0.2f)
                } else {
                    Color.LightGray.copy(0.0f)
                },
                shape = MaterialTheme.shapes.extraSmall
            )
            .clickable (
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onClick()
            }
            // Workaround to prevent the rich editor
            // from losing focus when clicking on the button
            // (Happens only on Desktop)
            .focusProperties { canFocus = false },
        contentAlignment = Alignment.Center

    ) {
        Icon(
            icon,
            contentDescription = icon.name,
            tint = if (isSelected) {
                Primary
            } else {
                Color.Black.copy(0.7f)
            },
            modifier = Modifier
                .size(23.dp)
        )
    }
}