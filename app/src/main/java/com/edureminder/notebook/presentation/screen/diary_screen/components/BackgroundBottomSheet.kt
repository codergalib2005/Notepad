package com.edureminder.notebook.presentation.screen.diary_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.notebook.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.notebook.presentation.screen.edit_note.backgrounds
import com.edureminder.notebook.ui.ColorBlack
import com.edureminder.notebook.ui.ColorWhite
import com.edureminder.notebook.ui.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    editorViewModel: NoteEditorViewModel
) {
    if (editorViewModel.showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState,
            dragHandle = null,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column (
                modifier = Modifier
                    .height(400.dp)
                    .shadow(
                        elevation = 5.dp
                    )
                    .background(Color.White)
                    .background(Primary.copy(0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(Modifier.size(20.dp).semantics {
                        contentDescription = "space box"
                    })
                    Text(
                        text = "Backgrounds & Theme",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = ColorBlack.copy(0.7f)
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                editorViewModel.showThemeSheet = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp),
                            tint = Primary
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    itemsIndexed(backgrounds){ index, back ->
                        Box (
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = MaterialTheme.shapes.medium,
                                    clip = true,
                                    ambientColor = ColorBlack.copy(0.2f)
                                )
                                .clip(MaterialTheme.shapes.medium)
                                .background(ColorWhite)
                                .background(
                                    if(index == 0) ColorWhite else Primary.copy(0.2f)
                                )
                                .clickable (
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    editorViewModel.selectedBackground = back
                                },
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Image(
                                painter = painterResource(id = back.res),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.FillBounds,
                                alignment = Alignment.Center
                            )
                            if(editorViewModel.selectedBackground.id == back.id) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Primary),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}