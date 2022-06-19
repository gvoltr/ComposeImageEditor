package com.gvoltr.compose_image_editor.draw

import androidx.compose.ui.graphics.toArgb
import com.gvoltr.compose_image_editor.ui.theme.Colors

object ColorOptions {

    val lineDrawingColors = listOf(
        Colors.Blue500.toArgb(),
        Colors.Green400.toArgb(),
        Colors.Pink400.toArgb(),
        Colors.Orange500.toArgb(),
    )

    val textDrawingColors = listOf(
        Colors.White.toArgb(),
        Colors.Gray800.toArgb(),
    )
}
