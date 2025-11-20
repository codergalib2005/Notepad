package com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.ui.Primary

@Composable
fun AnimatedCursorTextField(
    text: String,
    onTextChange: (String) -> Unit,
    hint: String = "Enter text...",
    enabled: Boolean = true
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        ),
        enabled = enabled,
        cursorBrush = SolidColor(Primary.copy(0.7f)), // We'll draw our custom cursor
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                innerTextField()
            }
        },
        modifier = Modifier
            .padding(horizontal = 13.dp)
    )
}