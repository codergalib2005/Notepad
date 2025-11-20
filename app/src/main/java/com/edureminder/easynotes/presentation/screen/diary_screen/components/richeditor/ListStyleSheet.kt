package com.edureminder.easynotes.presentation.screen.diary_screen.components.richeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.ui.Primary
import com.feature.edureminder.texteditor.model.RichTextState
import com.feature.edureminder.texteditor.paragraph.type.UnorderedListStyleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListStyleSheet(
    richTextState: RichTextState,
    editorViewModel: NoteEditorViewModel,
    sheetState: SheetState,
    onOpenKeyboard: () -> Unit
){
    val lists = listOf(
        UnorderedListStyleType.Disc,
        UnorderedListStyleType.Arrow,
        UnorderedListStyleType.Check,
        UnorderedListStyleType.Fire,
        UnorderedListStyleType.Star,
        UnorderedListStyleType.Watermelon,
        UnorderedListStyleType.Square,
        UnorderedListStyleType.Heart,
        UnorderedListStyleType.Cherry,
        UnorderedListStyleType.Apple,
        UnorderedListStyleType.RightFinger,
        UnorderedListStyleType.Strawberry,
    )

    if(editorViewModel.isListSelectorSheetOpen){
        ModalBottomSheet(
            onDismissRequest = {
                onOpenKeyboard()
            },
            dragHandle = {

            },
            shape = RoundedCornerShape(0.dp),
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(30.dp))
                    Text(
                        text = "List",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = {
                            onOpenKeyboard()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                }
                FlowRow(
                    maxItemsInEachRow = 3,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = if(richTextState.isOrderedList) Primary else Color.LightGray,
                                shape = MaterialTheme.shapes.small
                            )
                            .weight(1f)
                            .height(60.dp)
                            .clickable {
                                richTextState.toggleOrderedList()
                                editorViewModel.selectedEmoji = ""
                            }
                            .padding(vertical = 7.dp, horizontal = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(1, 2, 3).forEach { item ->
                            Row (
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = item.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End
                                )
                                Spacer(
                                    modifier = Modifier
                                        .padding(
                                            end = when(item){
                                                2 -> 10.dp
                                                3 -> 15.dp
                                                else -> 0.dp
                                            },
                                            start = if(item == 1) 3.dp else 0.dp)
                                        .weight(1f)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray.copy(0.7f))
                                )
                            }
                        }
                    }
                    lists.forEach { emoji ->
                        val currentType = richTextState.config.unorderedListStyleType
//                        val newType = UnorderedListStyleType.from(emoji)

                        Column(
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = Primary,//if(editorViewModel.selectedEmoji == emoji) Primary else Color.LightGray,
                                    shape = MaterialTheme.shapes.small
                                )
                                .weight(1f)
                                .height(60.dp)
                                .clickable {

//                                    if (currentType == newType) {
//
//                                        // Same emoji → turn OFF
//                                        richTextState.toggleUnorderedList()
//
//                                        // CLEAR selected emoji
//                                        editorViewModel.selectedEmoji = ""
//
//                                    } else {
//
//                                        // Save selected emoji
//                                        editorViewModel.selectedEmoji = emoji
//
//                                        // Reset existing list first
//                                        if (richTextState.isUnorderedList) {
//                                            richTextState.toggleUnorderedList()
//                                        }
//
//                                        // Set bullet type
//                                        richTextState.config.unorderedListStyleType = newType
//
//                                        // Turn list back ON
//                                        richTextState.toggleUnorderedList()
//                                    }
                                }
                                .padding(vertical = 7.dp, horizontal = 10.dp)
                        ) {
                            listOf(0, 1, 2).forEach { item ->
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
//                                    Text(
//                                        text = emoji,
//                                        fontSize = 12.sp,
//                                        fontWeight = FontWeight.Medium,
//                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .padding(end =when(item){
                                                1 -> 10.dp
                                                2 -> 15.dp
                                                else -> 0.dp
                                            })
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray.copy(0.7f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}