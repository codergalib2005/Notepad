package com.edureminder.easynotes.presentation.screen.diary_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.R
import com.edureminder.easynotes.presentation.screen.edit_note.NoteEditorViewModel
import com.edureminder.easynotes.ui.Primary
import com.edureminder.easynotes.ui.Purple40
import kotlinx.coroutines.launch


data class StickerGroup(
    val title: String,
    val list: List<Sticker>
)
data class Sticker(
    val id: Int,
    val sticker: Int,
    val paid: Int
)
data class Page(
    val id: Int,
    val icon: Int,
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickersStyleSheet(
    editorViewModel: NoteEditorViewModel,
    sheetState: SheetState,
    onOpenKeyboard: () -> Unit
){
    val stickers = listOf(
        StickerGroup(
            "Standard",
            listOf(
                Sticker(
                    1,
                    R.drawable.sticker_standard1,
                    0
                ),
                Sticker(
                    2,
                    R.drawable.sticker_standard2,
                    0
                ),

                Sticker(
                    3,
                    R.drawable.sticker_standard3,
                    0
                ),
                Sticker(
                    4,
                    R.drawable.sticker_standard4,
                    0
                ),
                Sticker(
                    5,
                    R.drawable.sticker_standard5,
                    0
                ),
                Sticker(
                    6,
                    R.drawable.sticker_standard6,
                    0
                ),
                Sticker(
                    7,
                    R.drawable.sticker_standard7,
                    1
                ),
                Sticker(
                    8,
                    R.drawable.sticker_standard8,
                    1
                ),
                Sticker(
                    9,
                    R.drawable.sticker_standard9,
                    1
                ),
                Sticker(
                    10,
                    R.drawable.sticker_standard10,
                    1
                ),
                Sticker(
                    11,
                    R.drawable.sticker_standard11,
                    1
                ),
                Sticker(
                    12,
                    R.drawable.sticker_standard12,
                    1
                ),
                Sticker(
                    13,
                    R.drawable.sticker_standard13,
                    1
                ),
                Sticker(
                    14,
                    R.drawable.sticker_standard14,
                    1
                ),
                Sticker(
                    15,
                    R.drawable.sticker_standard15,
                    1
                ),
                Sticker(
                    16,
                    R.drawable.sticker_standard16,
                    1
                ),
            )
        ),
        StickerGroup(
            "Cutie",
            listOf(
                Sticker(
                    17,
                    R.drawable.sticker_cutie1,
                    2
                ),
                Sticker(
                    18,
                    R.drawable.sticker_cutie2,
                    2
                ),
                Sticker(
                    19,
                    R.drawable.sticker_cutie3,
                    2
                ),
                Sticker(
                    20,
                    R.drawable.sticker_cutie4,
                    2
                ),
                Sticker(
                    21,
                    R.drawable.sticker_cutie5,
                    2
                ),
                Sticker(
                    22,
                    R.drawable.sticker_cutie6,
                    2
                ),
                Sticker(
                    23,
                    R.drawable.sticker_cutie7,
                    2
                ),
                Sticker(
                    24,
                    R.drawable.sticker_cutie8,
                    2
                ),
                Sticker(
                    25,
                    R.drawable.sticker_cutie9,
                    2
                ),
                Sticker(
                    26,
                    R.drawable.sticker_cutie10,
                    2
                ),
                Sticker(
                    27,
                    R.drawable.sticker_cutie11,
                    2
                ),
                Sticker(
                    28,
                    R.drawable.sticker_cutie12,
                    2
                ),
                Sticker(
                    29,
                    R.drawable.sticker_cutie13,
                    2
                ),
                Sticker(
                    30,
                    R.drawable.sticker_cutie14,
                    2
                ),
                Sticker(
                    31,
                    R.drawable.sticker_cutie15,
                    2
                ),
                Sticker(
                    32,
                    R.drawable.sticker_cutie16,
                    2
                ),
            )
        ),
        StickerGroup(
            "Emoji",
            listOf(
                Sticker(
                    33,
                    R.drawable.sticker_emoji1,
                    2
                ),
                Sticker(
                    34,
                    R.drawable.sticker_emoji2,
                    2
                ),
                Sticker(
                    35,
                    R.drawable.sticker_emoji3,
                    2
                ),
                Sticker(
                    36,
                    R.drawable.sticker_emoji4,
                    2
                ),
                Sticker(
                    37,
                    R.drawable.sticker_emoji5,
                    2
                ),
                Sticker(
                    38,
                    R.drawable.sticker_emoji6,
                    2
                ),
                Sticker(
                    39,
                    R.drawable.sticker_emoji7,
                    2
                ),
                Sticker(
                    40,
                    R.drawable.sticker_emoji8,
                    2
                ),
                Sticker(
                    41,
                    R.drawable.sticker_emoji9,
                    2
                ),
                Sticker(
                    42,
                    R.drawable.sticker_emoji10,
                    2
                ),
                Sticker(
                    43,
                    R.drawable.sticker_emoji11,
                    2
                ),
                Sticker(
                    44,
                    R.drawable.sticker_emoji12,
                    2
                ),
                Sticker(
                    45,
                    R.drawable.sticker_emoji13,
                    2
                ),
                Sticker(
                    46,
                    R.drawable.sticker_emoji14,
                    2
                ),
                Sticker(
                    47,
                    R.drawable.sticker_emoji15,
                    2
                ),
                Sticker(
                    48,
                    R.drawable.sticker_emoji16,
                    2
                ),
            )
        ),
        StickerGroup(
            "Watercolor",
            listOf(
                Sticker(
                    49,
                    R.drawable.sticker_watercolor1,
                    2,
                ),
                Sticker(
                    50,
                    R.drawable.sticker_watercolor2,
                    2,
                ),
                Sticker(
                    51,
                    R.drawable.sticker_watercolor3,
                    2,
                ),
                Sticker(
                    52,
                    R.drawable.sticker_watercolor4,
                    2,
                ),
                Sticker(
                    53,
                    R.drawable.sticker_watercolor5,
                    2,
                ),
                Sticker(
                    54,
                    R.drawable.sticker_watercolor6,
                    2,
                ),
                Sticker(
                    55,
                    R.drawable.sticker_watercolor7,
                    2,
                ),
                Sticker(
                    56,
                    R.drawable.sticker_watercolor8,
                    2,
                ),
                Sticker(
                    57,
                    R.drawable.sticker_watercolor9,
                    2,
                ),
                Sticker(
                    58,
                    R.drawable.sticker_watercolor10,
                    2,
                ),
                Sticker(
                    59,
                    R.drawable.sticker_watercolor11,
                    2,
                ),
                Sticker(
                    60,
                    R.drawable.sticker_watercolor12,
                    2,
                ),
                Sticker(
                    61,
                    R.drawable.sticker_watercolor13,
                    2,
                ),
                Sticker(
                    62,
                    R.drawable.sticker_watercolor14,
                    2,
                ),
                Sticker(
                    63,
                    R.drawable.sticker_watercolor15,
                    2,
                ),
                Sticker(
                    64,
                    R.drawable.sticker_watercolor16,
                    2,
                ),
            )
        )
    )

    val gridState = rememberLazyGridState()
    var activeGroupIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }.collect { index ->
            // Each group has 1 header + N stickers → we must calculate offsets
            var accumulated = 0
            stickers.forEachIndexed { groupIndex, group ->
                val groupSize = 1 + group.list.size  // 1 = title row in LazyGrid
                if (index >= accumulated && index < accumulated + groupSize) {
                    activeGroupIndex = groupIndex
                    return@collect
                }
                accumulated += groupSize
            }
        }
    }
    val groupStartIndexes = remember {
        val indexes = mutableListOf<Int>()
        var current = 0
        stickers.forEach { group ->
            indexes.add(current) // header index
            current += 1 + group.list.size
        }
        indexes
    }



    val pages = listOf(
        Page(
            1,
            R.drawable.sticker_standard1,
        ),
        Page(
            2,
            R.drawable.sticker_cutie1
        ),
        Page(
            3,
            R.drawable.sticker_emoji1
        ),
        Page(
            4,
            R.drawable.sticker_watercolor1
        )
    )

    if(editorViewModel.isStickersSelectorSheetOpen){
        ModalBottomSheet(
            onDismissRequest = {
                onOpenKeyboard()
            },
            dragHandle = null,
            shape = RoundedCornerShape(0.dp),
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,

                            )
                        .background(Color.White)
                        .height(55.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f).padding(start = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        pages.forEachIndexed { index, page ->
                            val isActive = index == activeGroupIndex
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .drawWithContent {
                                        drawContent()
                                        clipRect {
                                            val strokeWidth = 7f
                                            val y = size.height
                                            drawLine(
                                                brush = SolidColor(if (isActive) Primary else Color.White),
                                                strokeWidth = strokeWidth,
                                                cap = StrokeCap.Square,
                                                start = Offset.Zero.copy(y = y),
                                                end = Offset(x = size.width, y = y)
                                            )
                                        }
                                    }
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ){
                                        val targetIndex = groupStartIndexes[index]
                                        scope.launch {
                                            gridState.animateScrollToItem(targetIndex)
                                        }
                                    }
                                    .padding(horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = page.icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(45.dp)
                                )
                            }
                        }
                    }
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
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 15.dp, horizontal = 10.dp)
                ) {
                    stickers.forEach { group ->
                        item (
                            span = { GridItemSpan(4) }
                        ) {
                            Text(
                                text = group.title,
                                fontSize = 18.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                        items(group.list){ sticker ->
                            StickerItem(
                                sticker = sticker,
                                editorViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StickerItem(
    sticker: Sticker,
    editorViewModel: NoteEditorViewModel
) {
    Box (
        modifier = Modifier
            .size(95.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){
                editorViewModel.addImage(sticker.sticker)
                editorViewModel.isStickersSelectorSheetOpen = false
            },
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painterResource(id = sticker.sticker),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight(),
            contentScale = ContentScale.FillHeight
        )
        when(sticker.paid){
            0 -> {}
            1 -> {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(Purple40),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.play_icon_in_circle),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.2f)
                    )
                }
            }
            2 -> {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.crown_icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }
            }
        }
    }
}