package com.edureminder.easynotes.presentation.screen.main_screen.task_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edureminder.easynotes.ui.ColorBlack
import com.edureminder.easynotes.ui.ColorWhite
import com.edureminder.easynotes.ui.Primary


fun Modifier.crop(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    fun Dp.toPxInt(): Int = this.toPx().toInt()

    layout(
        placeable.width - (horizontal * 2).toPxInt(),
        placeable.height - (vertical * 2).toPxInt()
    ) {
        placeable.placeRelative(-horizontal.toPx().toInt(), -vertical.toPx().toInt())
    }
}

@Composable
fun MarkWithSymbol(
    isSymbolSheetOpen: Boolean,
    onUpdateSymbol: (Int) -> Unit,
    onDismissSymbolSheet: () -> Unit,
    onClearSymbol: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .crop(vertical = 8.dp),
        expanded = true,
        onDismissRequest = {
            onDismissSymbolSheet()
        },
        shape = MaterialTheme.shapes.large
    ) {
        Column (
            modifier = Modifier
                .width(225.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = MaterialTheme.shapes.large,
                    ambientColor = Primary.copy(0.6f),
                    spotColor = Primary.copy(0.6f)
                )
                .clip(MaterialTheme.shapes.large)
                .background(ColorWhite)
                .padding(10.dp),
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mark with symbol"
                )
                TextButton(
                    onClick = {}
                ) {
                    Text(
                        text = "Clear",
                        color = ColorBlack.copy(0.8f)
                    )
                }
            }
            Column (
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Symbols.symbols.groupBy { it.group }.forEach { symbols ->
                    Column {
                        Text(
                            text = when(symbols.key){
                                1 -> "Flag"
                                2 -> "Pie"
                                3 -> "Number"
                                4 -> "Number"
                                else -> ""
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorBlack
                        )
                        Row {
                            symbols.value.forEach { symbol ->
                                IconButton(
                                    onClick = {
                                        onUpdateSymbol(symbol.id)
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    symbol.icon()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}