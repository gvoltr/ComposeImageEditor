package com.gvoltr.compose_image_editor.draw

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingLayer(
    modifier: Modifier,
    state: PhotoEditorState,
    actioner: (PhotoEditorAction) -> Unit,
) {
    Canvas(
        modifier = modifier
            .background(Color.Transparent)
            .pointerInteropFilter {
                if (state.currentOperation is DrawingOperation.LineDrawing) {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            actioner(PhotoEditorAction.StartLineDrawing(Point(it.x, it.y)))
                        }
                        MotionEvent.ACTION_MOVE -> {
                            actioner(PhotoEditorAction.MoveLineDrawing(Point(it.x, it.y)))
                        }
                        MotionEvent.ACTION_UP -> {
                            actioner(PhotoEditorAction.StopLineDrawing)
                        }
                        else -> return@pointerInteropFilter false
                    }
                }
                true
            }
    ) {
        state.drawings.filterIsInstance(Drawing.Line::class.java).forEach {
            drawPath(
                it.path,
                state.strokeWidth,
                Color(it.color)
            )
        }

        if (state.currentOperation is DrawingOperation.LineDrawing) {
            state.currentOperation.line?.let {
                drawPath(
                    it.path,
                    state.strokeWidth,
                    Color(it.color)
                )
            }
        }
    }
}

private fun DrawScope.drawPath(path: Path, strokeWidthPx: Float, color: Color) {
    drawPath(
        path,
        color,
        style = Stroke(
            width = strokeWidthPx,
            join = StrokeJoin.Round,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.cornerPathEffect(90f)
        ),
    )
}