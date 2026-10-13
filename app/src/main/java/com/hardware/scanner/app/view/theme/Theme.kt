package com.hardware.scanner.app.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimary,
    onPrimary = Color.White,
    secondary = Purple80,
    tertiary = Pink80,
    background = AppSurfaceDark,
    onBackground = AppOnSurfaceDark,
    surface = AppSurfaceDark,
    onSurface = AppOnSurfaceDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFBAC6D5),
    primaryContainer = Color(0xFF1B3A5F),
    onPrimaryContainer = Color(0xFFEAF2FF)
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = Color.White,
    secondary = Purple40,
    tertiary = Pink40,
    background = AppSurfaceLight,
    onBackground = AppOnSurfaceLight,
    surface = Color.White,
    onSurface = AppOnSurfaceLight,
    surfaceVariant = AppSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF4B5563),
    primaryContainer = Color(0xFFDBEBFF),
    onPrimaryContainer = Color(0xFF0F172A)
)

@Composable
fun HardwareScannerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}