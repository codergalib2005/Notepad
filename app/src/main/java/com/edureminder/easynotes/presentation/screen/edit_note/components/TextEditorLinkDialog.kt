package com.edureminder.easynotes.presentation.screen.edit_note.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.ui.Primary
import com.feature.edureminder.texteditor.model.RichTextState

@Composable
fun TextEditorLinkDialog(
    state: RichTextState,
    openLinkDialog: MutableState<Boolean>,
) {
    var text by remember { mutableStateOf(state.selectedLinkText.orEmpty()) }
    var link by remember { mutableStateOf(state.selectedLinkUrl.orEmpty()) }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 5.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.size(25.dp))
            Text(
                text = "Add Link",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(0.7f)
            )
            Box (
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ){
                        openLinkDialog.value = false
                        text = ""
                        link = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
        OutlinedTextField(
            value =
                if (state.selection.collapsed)
                    text
                else
                    state.annotatedString.text.substring(
                        state.selection.min,
                        state.selection.max
                    ),
            onValueChange = {
                text = it
            },
            label = {
                Text(
                    text = "Text",
                    color = Color.Black.copy(0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black.copy(0.2f),
                unfocusedBorderColor = Color.Black.copy(0.2f)
            ),
            enabled = state.selection.collapsed && !state.isLink,
            modifier = Modifier
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = link,
            onValueChange = {
                link = it
            },
            label = {
                Text(
                    text = "Link",
                    color = Color.Black.copy(0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black.copy(0.2f),
                unfocusedBorderColor = Color.Black.copy(0.2f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End)
        ) {
            if (state.isLink) {
                OutlinedButton(
                    onClick = {
                        state.removeLink()
                        openLinkDialog.value = false
                        text = ""
                        link = ""
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Primary
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Primary
                    ),
                    shape =  MaterialTheme.shapes.extraSmall,
                    modifier = Modifier
                ) {
                    Text(
                        text = "Remove",
                        color = Primary
                    )
                }
            }
            Button(
                onClick = {
                    when {
                        state.isLink ->
                            state.updateLink(
                                url = link,
                            )

                        state.selection.collapsed ->
                            state.addLink(
                                text = text,
                                url = link
                            )

                        else ->
                            state.addLinkToSelection(
                                url = link
                            )
                    }

                    openLinkDialog.value = false
                    text = ""
                    link = ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                ),
                enabled = (text.isNotEmpty() || !state.selection.collapsed) && link.isNotEmpty(),
                shape =  MaterialTheme.shapes.extraSmall,
                modifier = Modifier
            ) {
                Text(
                    text = "Save",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
    }
}