package com.slopeos.launcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB0C4FF),
    secondary = Color(0xFF9BB7FF),
    background = Color(0xFF0A0A0E),
    surface = Color(0x3318181C),
    onSurface = Color(0xFFE8EAEE)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF2D3E61),
    secondary = Color(0xFF3A4D73),
    background = Color(0xFFF6F7FA),
    surface = Color(0x66FFFFFF),
    onSurface = Color(0xFF0E1116)
)

@Composable
fun SlopeOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
