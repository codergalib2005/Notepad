package com.edureminder.easynotes.presentation.screen.main_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.edureminder.easynotes.R
import com.edureminder.easynotes.presentation.navigation.Screen
import com.edureminder.easynotes.ui.Primary
import androidx.compose.ui.platform.LocalResources

class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }
        val cornerDiameter = cornerRadiusPx * 2
        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 2.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            // bottom left
            moveTo(x = 0F, y = size.height)
            // top left
            if (cutoutLeftX > 0) {
                val realLeftCornerDiameter = if (cutoutLeftX >= cornerRadiusPx) {
                    // there is a space between rounded corner and cutout
                    cornerDiameter
                } else {
                    // rounded corner and cutout overlap
                    cutoutLeftX * 2
                }
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = realLeftCornerDiameter,
                        bottom = realLeftCornerDiameter
                    ),
                    startAngleDegrees = 180.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            lineTo(cutoutLeftX, 0f)
            // cutout
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )
            // top right
            if (cutoutRightX < size.width) {
                val realRightCornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx) {
                    cornerDiameter
                } else {
                    (size.width - cutoutRightX) * 2
                }
                arcTo(
                    rect = Rect(
                        left = size.width - realRightCornerDiameter,
                        top = 0f,
                        right = size.width,
                        bottom = realRightCornerDiameter
                    ),
                    startAngleDegrees = -90.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            // bottom right
            lineTo(x = size.width, y = size.height)
            close()
        }
    }
}
@Composable
fun RowScope.AddItem(
    route: Screen,
    title: String,
    icon: Int,
    selectedIcon: Int,
    selected: Boolean,
    onTabSelected: () -> Unit
) {

    NavigationBarItem(
        icon = {
            Icon(
                painter = painterResource(id = if(selected) selectedIcon else icon),
                contentDescription = "Navigation Icon",
                modifier = Modifier.size(25.dp),
                tint = if(selected) Primary else Color.Gray
            )
        },
        label = {
            Text(
                text = title,
                letterSpacing = if(selected) 1.sp else 0.1.sp,
                color = if(selected) Primary else Color.Gray
            )
        },

        selected = selected,
        onClick = {
            onTabSelected()
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Black,
            unselectedIconColor = Color.Gray,
            selectedTextColor = Color.Black,
            unselectedTextColor = Color.Gray,
            indicatorColor = Color.White
        )
    )
}
enum class BottomNavItems(
    val route: Screen,
    val selectedIcon: Int,
    val icon: Int,
    val title: String,
) {
    REPORTS(
        route = Screen.MainScreen,
        R.drawable.home,
        R.drawable.home_outline,
        "Home"
    ),
    HOME(
        route = Screen.MainScreen,
        R.drawable.task,
        R.drawable.task_outline,
        "Task"
    ),
    NULLS(
        route = Screen.MainScreen,
        0,
        0,
        ""
    ),
    SHIFTS(
        route = Screen.MainScreen,
        R.drawable.diary,
        R.drawable.diary_outline,
        "Diary"
    ),
    PROFILE(
        route = Screen.MainScreen,
        R.drawable.settings,
        R.drawable.setting_outline,
        "Settings"
    ),


}
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val withPx = LocalResources.current.displayMetrics.widthPixels

    AnimatedVisibility(
        visible = true,
    ) {
        val barShape = BarShape(
            offset = withPx / 2f,
            circleRadius = 30.dp,
            cornerRadius = 15.dp,
            circleGap = 10.dp,
        )

        Box(
            modifier = Modifier
                .clip(barShape)
                .background(shape = barShape, color = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(80.dp)
                    .graphicsLayer {
                        shape = barShape
                        clip = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItems.entries.forEachIndexed { index, screen ->
                    if(screen.title == ""){
                        Spacer(
                            modifier = Modifier
                                .size(80.dp)
                        )
                    } else {
                        AddItem(
                            route = screen.route,
                            title = screen.title,
                            icon = screen.icon,
                            selectedIcon = screen.selectedIcon,
                            selected = selectedTab == index,
                            onTabSelected = {
                                onTabSelected(index)
                            }
                        )
                    }
                }
            }
        }
    }
}