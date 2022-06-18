package com.gvoltr.compose_image_editor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val LightColorPalette = lightColors(
    primary = Colors.Blue600,
    primaryVariant = Colors.Blue700,
    secondary = Colors.Green500,
    secondaryVariant = Colors.Green600,
    background = Colors.Gray50,
    surface = Colors.Gray50,
    error = Colors.Red500,
    onPrimary = Colors.Gray50,
    onSecondary = Colors.Gray900,
    onBackground = Colors.Gray900,
    onSurface = Colors.Gray900,
    onError = Colors.Gray50
)

private val DarkColorPalette = darkColors(
    primary = Colors.Blue600,
    primaryVariant = Colors.Blue700,
    secondary = Colors.Green500,
    secondaryVariant = Colors.Green600,
    background = Colors.Gray50,
    surface = Colors.Gray50,
    error = Colors.Red500,
    onPrimary = Colors.Gray50,
    onSecondary = Colors.Gray900,
    onBackground = Colors.Gray900,
    onSurface = Colors.Gray900,
    onError = Colors.Gray50
)

@Composable
fun ComposeImageEditorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}