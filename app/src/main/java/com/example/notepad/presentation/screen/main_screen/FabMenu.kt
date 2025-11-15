package com.example.notepad.presentation.screen.main_screen

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.notepad.ui.theme.Primary


data class TaskTypeItem(
    val id: Int,
    val name: String,
    val icon: Int,
    val onClick: ()-> Unit
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenu(
    tasksTypes: List<TaskTypeItem>
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = { expanded = it },
                containerColor = { progress ->
                    // progress: 0f -> collapsed, 1f -> expanded
                    // You can animate or interpolate between two colors here
                    if (progress < 0.5f)
                        Primary
                    else
                        Primary
                },
            ) {
                Icon(
                    imageVector =  if(expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        tasksTypes.forEach { item ->
            FloatingActionButtonMenuItem(
                onClick = {
                    expanded = false
                    item.onClick()
                },
                text = { Text(item.name) },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                    )
                },
                containerColor = Primary,
                contentColor = Color.White
            )
        }
    }
}