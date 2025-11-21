package com.edureminder.easynotes.presentation.screen.main_screen.task_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.edureminder.easynotes.R

data class Symbol(
    val id: Int,
    val icon:  @Composable () -> Unit,
    val group: Int,
)
object Symbols {
    val symbols = listOf(
        Symbol(
            id = 1,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol_flag),
                    contentDescription = null,
                    tint = Color("#34656D".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 1,
        ),
        Symbol(
            id = 2,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol_flag),
                    contentDescription = null,
                    tint = Color("#BF124D".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 1,
        ),
        Symbol(
            id = 3,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol_flag),
                    contentDescription = null,
                    tint = Color("#73AF6F".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 1,
        ),
        Symbol(
            id = 4,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol_flag),
                    contentDescription = null,
                    tint = Color("#E83C91".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 1,
        ),
        Symbol(
            id = 5,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol_flag),
                    contentDescription = null,
                    tint = Color("#FFA239".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 1,
        ),
        Symbol(
            id = 6,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol_flag),
                    contentDescription = null,
                    tint = Color("#EE6983".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 1,
        ),
        Symbol(
            id = 7,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol7),
                    contentDescription = null,
                    tint = Color("#34656D".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 2,
        ),
        Symbol(
            id = 8,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol8),
                    contentDescription = null,
                    tint = Color("#BF124D".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 2,
        ),
        Symbol(
            id = 9,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol9),
                    contentDescription = null,
                    tint = Color("#73AF6F".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 2,
        ),
        Symbol(
            id = 10,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol10),
                    contentDescription = null,
                    tint = Color("#E83C91".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 2,
        ),
        Symbol(
            id = 11,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol11),
                    contentDescription = null,
                    tint = Color("#FFA239".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 2,
        ),
        Symbol(
            id = 12,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol12),
                    contentDescription = null,
                    tint = Color("#EE6983".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 2,
        ),
        Symbol(
            id = 13,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol13),
                    contentDescription = null,
                    tint = Color("#34656D".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 3,
        ),
        Symbol(
            id = 14,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol14),
                    contentDescription = null,
                    tint = Color("#BF124D".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 3,
        ),
        Symbol(
            id = 15,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol15),
                    contentDescription = null,
                    tint = Color("#73AF6F".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 3,
        ),
        Symbol(
            id = 16,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol16),
                    contentDescription = null,
                    tint = Color("#E83C91".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 3,
        ),
        Symbol(
            id = 17,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol17),
                    contentDescription = null,
                    tint = Color("#FFA239".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 3,
        ),
        Symbol(
            id = 18,
            {
                Icon(
                    painter = painterResource(id = R.drawable.task_symbol18),
                    contentDescription = null,
                    tint = Color("#EE6983".toColorInt()),
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 3,
        ),
        Symbol(
            id = 19,
            {
                Image(
                    painter = painterResource(id = R.drawable.task_symbol1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 4,
        ),
        Symbol(
            id = 20,
            {
                Image(
                    painter = painterResource(id = R.drawable.task_symbol2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 4,
        ),
        Symbol(
            id = 21,
            {
                Image(
                    painter = painterResource(id = R.drawable.task_symbol3),
                    contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 4,
        ),
        Symbol(
            id = 22,
            {
                Image(
                    painter = painterResource(id = R.drawable.task_symbol4),
                    contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 4,
        ),
        Symbol(
            id = 23,
            {
                Image(
                    painter = painterResource(id = R.drawable.task_symbol5),
                    contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 4,
        ),
        Symbol(
            id = 24,
            {
                Image(
                    painter = painterResource(id = R.drawable.task_symbol6),
                    contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                )
            },
            group = 4,
        ),
    )
}