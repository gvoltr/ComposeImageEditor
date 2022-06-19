package com.gvoltr.compose_image_editor.media.bitmap

sealed interface BitmapDrawing {
    data class Line(
        val points: List<PercentagePoint>,
        val color: Int,
        // Thickness of the line related to the width of image
        val relativeLineThickness: Float
    ) : BitmapDrawing

    data class Text(
        val text: String,
        val percentageCenterPosition: PercentagePoint = PercentagePoint(0f, 0f),
        val color: Int,
        val relativeFontSize: Float
    ) : BitmapDrawing
}
