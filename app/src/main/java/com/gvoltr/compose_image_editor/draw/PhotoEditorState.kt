package com.gvoltr.compose_image_editor.draw

import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntSize
import com.gvoltr.compose_image_editor.base.State

data class PhotoEditorState(
    // Finished drawings
    val drawings: List<Drawing> = emptyList(),
    // in progress drawing
    val currentOperation: DrawingOperation = DrawingOperation.LineDrawing(),
    val image: Image = Image(),
    val canvasSize: Size = Size(0f, 0f),
    val strokeWidth: Float = 0f,
    val drawingColors: DrawingColors = DrawingColors(emptyList(), 0),
    val fontSize: FontSizeSp = FontSizeSp(emptyList(), 0)
) : State {
    companion object {
        val Empty = PhotoEditorState()
    }
}

data class DrawingColors(val colors: List<Int>, val selectedColor: Int)
data class FontSizeSp(val sizes: List<Int>, val selectedSize: Int)
data class Size(val width: Float, val height: Float)

data class Image(
    val uri: Uri = Uri.EMPTY,
    val size: IntSize = IntSize.Zero,
    val aspectRatio: Float = 0f
)

sealed class Drawing {
    data class Line(
        val path: Path,
        val points: List<Point>,
        val color: Int
    ) : Drawing()

    data class Text(
        val text: String,
        val position: Point,
        val textBox: Rect,
        val color: Int,
        val fontSize: Int
    ) : Drawing()
}

sealed class DrawingOperation {
    // If line is null there is no active line drawing happening atm.
    data class LineDrawing(val line: Drawing.Line? = null) : DrawingOperation()
    data class TextDrawing(val text: Drawing.Text? = null) : DrawingOperation()
}
