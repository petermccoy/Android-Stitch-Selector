package com.stitchselector.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6B4E9B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF25005A),
    secondary = Color(0xFF4F7A5C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3EBD8),
    onSecondaryContainer = Color(0xFF0C2014),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD2BCFF),
    onPrimary = Color(0xFF3B1E6B),
    primaryContainer = Color(0xFF523583),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFB7CFBC),
    onSecondary = Color(0xFF213528),
    secondaryContainer = Color(0xFF384C3E),
    onSecondaryContainer = Color(0xFFD3EBD8),
)

@Composable
fun StitchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
