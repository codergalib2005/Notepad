package com.edureminder.notebook.presentation.screen.diary_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.edureminder.notebook.R
import com.edureminder.notebook.lib.toPx
import com.edureminder.notebook.presentation.screen.edit_note.CanvasObject
import com.edureminder.notebook.presentation.screen.edit_note.OffsetC
import kotlin.math.roundToInt

@Composable
fun CanvasItem(
    item: CanvasObject,
    onUpdate: (CanvasObject) -> Unit,
    onDelete: (CanvasObject) -> Unit,
    onSelect: (CanvasObject) -> Unit
) {
    var offset by remember { mutableStateOf(item.offset) }
    var rotation by remember { mutableStateOf(item.rotation) }
    var scale by remember { mutableStateOf(item.scale) }

    BoxWithConstraints {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val itemSizePx = 140.dp.toPx()

        Box(
            modifier = Modifier
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .graphicsLayer(
                    rotationZ = rotation,
                    scaleX = scale,
                    scaleY = scale
                )
                // Tap to select
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            onSelect(item)    // 🔥 select immediately
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()

                            var newX = offset.x + dragAmount.x
                            var newY = offset.y + dragAmount.y

                            val scaledSize = itemSizePx * scale
                            newX = newX.coerceIn(0f, maxWidthPx - scaledSize)
                            newY = newY.coerceIn(0f, maxHeightPx - scaledSize)

                            offset = OffsetC(newX, newY)
                            onUpdate(item.copy(offset = offset))
                        }
                    )
                }

        ) {
            // Dotted border if selected
            if (item.isSelected) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val dash = floatArrayOf(12f, 12f)
                            drawRoundRect(
                                color = Color.LightGray,
                                style = Stroke(
                                    width = strokeWidth,
                                    pathEffect = PathEffect.dashPathEffect(dash)
                                ),
                                cornerRadius = CornerRadius(30f)
                            )
                        }
                )
            }

            // Image
            Image(
                painter = painterResource(id = item.res),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            // Controllers
            if (item.isSelected) {
                Icon(
                    painterResource(id = R.drawable.close),
                    contentDescription = "",
                    tint = Color.Red,
                    modifier = Modifier
                        .offset(x = (-10).dp, y = (-10).dp)
                        .align(Alignment.TopStart)
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onDelete(item) }
                )

                Icon(
                    painterResource(id = R.drawable.rotate),
                    contentDescription = "",
                    tint = Color(0xFFFF6FB5),
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .align(Alignment.TopEnd)
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                rotation += dragAmount.x
                                onUpdate(item.copy(rotation = rotation))
                            }
                        }
                )

                Icon(
                    painterResource(id = R.drawable.resize),
                    contentDescription = "",
                    tint = Color.Blue,
                    modifier = Modifier
                        .offset(x = 10.dp, y = (10).dp)
                        .align(Alignment.BottomEnd)
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                scale += dragAmount.x * 0.01f
                                scale = scale.coerceIn(0.5f, 3f)
                                onUpdate(item.copy(scale = scale))
                            }
                        }
                )
            }
        }
    }
}