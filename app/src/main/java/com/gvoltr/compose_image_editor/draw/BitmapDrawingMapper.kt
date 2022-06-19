package com.gvoltr.compose_image_editor.draw

import com.gvoltr.compose_image_editor.base.DimensionUtil
import com.gvoltr.compose_image_editor.media.bitmap.BitmapDrawing
import com.gvoltr.compose_image_editor.media.bitmap.PercentagePoint

class BitmapDrawingMapper(
    private val canvasSize: Size,
    private val strokeWidth: Float,
    private val dimensionUtil: DimensionUtil,
)  {

    fun map(input: Drawing) = when (input) {
        is Drawing.Line -> mapLine(input)
        is Drawing.Text -> mapText(input)
    }

    private fun mapLine(line: Drawing.Line) = BitmapDrawing.Line(
        points = line.points.map { it.toPercentagePoint() },
        color = line.color,
        relativeLineThickness = strokeWidth / canvasSize.width,
    )

    private fun mapText(text: Drawing.Text) = BitmapDrawing.Text(
        text = text.text,
        color = text.color,
        percentageCenterPosition = Point(
            text.position.x + text.textBox.width / 2,
            text.position.y + text.textBox.height / 2
        ).toPercentagePoint(),
        relativeFontSize = dimensionUtil.convertSpToPixel(text.fontSize.toFloat()) /
                canvasSize.width
    )

    private fun Point.toPercentagePoint(): PercentagePoint {
        return PercentagePoint(
            x / canvasSize.width,
            y / canvasSize.height
        )
    }
}