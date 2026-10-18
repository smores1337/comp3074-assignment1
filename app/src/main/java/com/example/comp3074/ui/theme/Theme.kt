package com.example.comp3074.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Black background with white buttons and text, same in light and dark mode
private val BlackWhiteColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    secondary = White,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
    onSurfaceVariant = LightGrey,
    outline = Grey,
    error = ErrorRed
)

@Composable
fun Comp3074Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlackWhiteColorScheme,
        typography = Typography,
        content = content
    )
}
