package com.edureminder.easynotes.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TurnLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.edureminder.easynotes.ui.Primary
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialTimePicker(
    context: Context,
    isShowing: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    is24HourField: Boolean,
    onTimeSelected: (String) -> Unit
) {
    val currentTime = LocalTime.now()
    val timePickerState = rememberTimePickerState(
        is24Hour = is24HourField,
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute
    )

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    if (isShowing.value) {
        Dialog(
            onDismissRequest = {
                isShowing.value = false
                val selectedHour = timePickerState.hour
                val selectedMinute = timePickerState.minute
                val time = LocalTime.of(selectedHour, selectedMinute)
                val formattedTime = time.format(timeFormatter)
                onTimeSelected(formattedTime)
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true // Ensures no dim shadow is applied
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(
                            topEnd = 25.dp,
                            topStart = 25.dp
                        )
                    )
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box (
                        modifier = Modifier
                            .size(45.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ) {
                                isShowing.value = false
                                val selectedHour = timePickerState.hour
                                val selectedMinute = timePickerState.minute
                                val time = LocalTime.of(selectedHour, selectedMinute)
                                val formattedTime = time.format(timeFormatter)
                                onTimeSelected(formattedTime)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TurnLeft,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(30.dp))
                val selectedHour = timePickerState.hour
                val selectedMinute = timePickerState.minute
                val time = LocalTime.of(selectedHour, selectedMinute)
                val formattedTime = time.format(timeFormatter)
                Text(
                    text = "Remind me at ${formattedTime.ifEmpty { "No Reminder" }}",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(0.8f)
                    )
                )

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = timePickerState,
                        modifier = modifier,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Primary.copy(0.1f),
                            clockDialUnselectedContentColor = Color.Black,
                            clockDialSelectedContentColor = Color.White,
                            selectorColor = Primary,
                            timeSelectorSelectedContainerColor = Primary.copy(0.9f),
                            timeSelectorUnselectedContentColor = Primary,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContainerColor = Primary.copy(0.1f),
                            periodSelectorBorderColor = Primary,
                            periodSelectorSelectedContentColor = Color.White,
                            periodSelectorUnselectedContainerColor = Primary.copy(0.1f),
                            periodSelectorSelectedContainerColor = Primary,
                            periodSelectorUnselectedContentColor = Primary,
                            containerColor = Primary,
                        )
                    )
                }
            }
        }
    }
}