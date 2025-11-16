package com.edureminder.easynotes.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.edureminder.easynotes.ui.Typography
import androidx.compose.material3.darkColorScheme
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

val SystemTypography = Typography()

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun NotepadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    useSystemFont: Boolean = true,
    isTheme: Int,
    dynamicColor: Boolean = true,
    isDarkMode: Int,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isSystemDarkMode = remember {
        context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkMode == 1) dynamicLightColorScheme(context) else dynamicLightColorScheme(context)

        }

        isDarkMode == 1 -> DarkColorScheme
        else -> LightColorScheme
    }

    when (isDarkMode) {
        1 -> darkColorScheme(isTheme)  // Dark mode
        2 -> if (isSystemDarkMode) darkColorScheme(isTheme) else lightColorScheme(isTheme)  // System mode
        else -> lightColorScheme(isTheme)   // Light mode by default (0 or any other value)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Primary.toArgb()
            window.navigationBarColor = ColorWhite.copy(0.3f).toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if(!useSystemFont) Poppins else SystemTypography,
        content = content,
    )
}