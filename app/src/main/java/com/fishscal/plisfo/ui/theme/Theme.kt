package com.fishscal.plisfo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = Yellow,
    tertiary = LightBlue
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = Yellow,
    tertiary = LightBlue,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = TextPrimary,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun FishScalePlusTheme(
    darkTheme: Boolean = false, // Always use light theme as requested
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}