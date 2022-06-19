package com.gvoltr.compose_image_editor.media.bitmap

import android.graphics.*
import android.net.Uri
import android.util.Log
import androidx.core.graphics.applyCanvas
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import javax.inject.Inject

class ImageEditor @Inject constructor(
    private val bitmapUtil: BitmapUtil
) {

    private val logTag = "ImageEditor"

    suspend fun drawOnImage(
        reference: Uri,
        outputImage: Uri,
        drawings: List<BitmapDrawing>
    ) {
        withContext(Dispatchers.IO) {
            val bitmap = bitmapUtil.getBitmap(reference) ?: return@withContext
            bitmap.applyCanvas {
                drawings.forEach {
                    when (it) {
                        is BitmapDrawing.Line -> drawLine(it)
                        is BitmapDrawing.Text -> drawText(it)
                    }
                }
            }
            val result = kotlin.runCatching {
                FileOutputStream(outputImage.toFile()).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    it.flush()
                }
            }
            bitmap.recycle()
            if (result.isFailure) {
                Log.e(logTag, result.toString())
            }
        }
    }

    private fun Canvas.drawLine(line: BitmapDrawing.Line) {
        val paint = Paint()
        paint.color = line.color
        paint.strokeWidth = line.relativeLineThickness * width
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.pathEffect = CornerPathEffect(90f)
        val path = Path().apply {
            moveTo(line.points.first().x * width, line.points.first().y * height)
            line.points.forEachIndexed { index, point ->
                if (index > 0) {
                    lineTo(point.x * width, point.y * height)
                }
            }
        }
        drawPath(path, paint)
    }

    private fun Canvas.drawText(text: BitmapDrawing.Text) {
        val textSize = text.relativeFontSize * width
        val paint = Paint()
        paint.color = text.color
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = textSize

        drawText(
            text.text,
            text.percentageCenterPosition.x * width,
            text.percentageCenterPosition.y * height + textSize / 2,
            paint
        )
    }
}