// File: NavigationAnimations.kt
package com.edureminder.easynotes.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset

// ====================
// Horizontal Slide
// ====================
fun slideLeftEnter() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideLeftExit() = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

fun slideRightEnter() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideRightExit() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

// ====================
// Vertical Slide
// ====================
fun slideUpEnter() = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideUpExit() = slideOutVertically(
    targetOffsetY = { -it },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

fun slideDownEnter() = slideInVertically(
    initialOffsetY = { -it },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideDownExit() = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

// ====================
// Diagonal Slides
// ====================
fun slideLeftUpEnter(): EnterTransition = slideIn(
    initialOffset = { fullSize -> IntOffset(fullSize.width, fullSize.height) },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideLeftUpExit(): ExitTransition = slideOut(
    targetOffset = { fullSize -> IntOffset(-fullSize.width, -fullSize.height) },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

fun slideRightUpEnter(): EnterTransition = slideIn(
    initialOffset = { fullSize -> IntOffset(-fullSize.width, fullSize.height) },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideRightUpExit(): ExitTransition = slideOut(
    targetOffset = { fullSize -> IntOffset(fullSize.width, -fullSize.height) },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

fun slideLeftDownEnter(): EnterTransition = slideIn(
    initialOffset = { fullSize -> IntOffset(fullSize.width, -fullSize.height) },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideLeftDownExit(): ExitTransition = slideOut(
    targetOffset = { fullSize -> IntOffset(-fullSize.width, fullSize.height) },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

fun slideRightDownEnter(): EnterTransition = slideIn(
    initialOffset = { fullSize -> IntOffset(-fullSize.width, -fullSize.height) },
    animationSpec = tween(500)
) + fadeIn(animationSpec = tween(500))

fun slideRightDownExit(): ExitTransition = slideOut(
    targetOffset = { fullSize -> IntOffset(fullSize.width, fullSize.height) },
    animationSpec = tween(500)
) + fadeOut(animationSpec = tween(500))

// ====================
// Fade
// ====================
fun fadeEnter() = fadeIn(animationSpec = tween(400))
fun fadeExit() = fadeOut(animationSpec = tween(400))

// ====================
// Scale
// ====================
fun scaleIn() = scaleIn(initialScale = 0.8f, animationSpec = tween(400)) + fadeIn(tween(400))
fun scaleOut() = scaleOut(targetScale = 0.8f, animationSpec = tween(400)) + fadeOut(tween(400))

// ====================
// Expand / Shrink
// ====================
fun expandIn() = expandIn(expandFrom = Alignment.Center, animationSpec = tween(400)) + fadeIn(tween(400))
fun shrinkOut() = shrinkOut(shrinkTowards = Alignment.Center, animationSpec = tween(400)) + fadeOut(tween(400))
