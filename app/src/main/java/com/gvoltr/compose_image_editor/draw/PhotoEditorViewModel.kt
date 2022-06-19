package com.gvoltr.compose_image_editor.draw

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.SavedStateHandle
import com.gvoltr.compose_image_editor.base.BaseViewModel
import com.gvoltr.compose_image_editor.base.BitmapUtil
import com.gvoltr.compose_image_editor.base.DimensionUtil
import com.gvoltr.compose_image_editor.draw.ColorOptions.lineDrawingColors
import com.gvoltr.compose_image_editor.draw.ColorOptions.textDrawingColors
import com.gvoltr.compose_image_editor.navigation.Navigator
import com.gvoltr.compose_image_editor.state.MediaStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoEditorViewModel @Inject constructor(
    private val bitmapUtil: BitmapUtil,
    private val dimensionUtil: DimensionUtil,
    private val mediaStateHolder: MediaStateHolder,
    private val savedStateHandle: SavedStateHandle,
    private val navigator: Navigator
) : BaseViewModel<PhotoEditorState, PhotoEditorAction, Unit>(PhotoEditorState.Empty) {

    init {
        setupImage()
        setupDrawing()
    }

    @SuppressWarnings("ComplexMethod")
    override suspend fun onAction(action: PhotoEditorAction) {
        when (action) {
            PhotoEditorAction.Save -> saveDrawings()
            is PhotoEditorAction.CanvasSizeChange -> {
                setState { copy(canvasSize = Size(action.width, action.height)) }
            }
            is PhotoEditorAction.StartLineDrawing -> startLineDrawing(action.point)
            is PhotoEditorAction.MoveLineDrawing -> moveLineDrawing(action.point)
            is PhotoEditorAction.StopLineDrawing -> stopLineDrawing()
            PhotoEditorAction.Undo -> undo()
            PhotoEditorAction.ClearEditing -> clearEditing()
            is PhotoEditorAction.SelectColor -> selectColor(action.color)
            is PhotoEditorAction.SelectFontSize -> selectFontSize(action.fontSize)
            PhotoEditorAction.SelectDrawingMode -> selectDrawingMode()
            PhotoEditorAction.SelectTextMode -> selectTextMode()
        }
    }

    private fun setupImage() {
        val selectedMediaFilename = savedStateHandle.get<String>(PhotoEditorDestination.argSelectedPhoto).orEmpty()

        val media = mediaStateHolder
            .currentValue
            .capturedMedia
            .first { it.uri.lastPathSegment == selectedMediaFilename }

        val uri = media.uri
        val size = bitmapUtil.getBitmapSize(uri)

        setState {
            copy(
                image = Image(uri, size, size.width.toFloat() / size.height.toFloat())
            )
        }
    }

    private fun setupDrawing() {
        val strokeWidthPx = dimensionUtil.convertDpToPixel(6f)
        setState {
            copy(
                strokeWidth = strokeWidthPx,
                fontSize = FontSizeSp(listOf(13, 18), 13),
                drawingColors = DrawingColors(
                    lineDrawingColors,
                    lineDrawingColors.first()
                )
            )
        }
    }

    private suspend fun saveDrawings() {
        stopLineDrawing()
//
//        val output = drawOnImageUseCase.invoke(
//            DrawOnImageUseCase.Params(
//                currentValue.image.uri,
//                BitmapDrawingMapper(
//                    currentValue.canvasSize,
//                    currentValue.strokeWidth,
//                    dimensionUtil
//                ).mapList(currentValue.drawings)
//            )
//        )
//        if (output !is InvokeSuccess) {
//            // TODO: report drawing failure
//            return
//        }
//        val mediaFile = LocalFile(output.data, UriType.File, FileType.Image)
//        inspectionsStateHolder.setState {
//            copy(
//                userAttachedFiles = userAttachedFiles.reduce(
//                    predicate = { it.techRecommendationId == checkId },
//                    reducer = {
//                        copy(
//                            mediaFiles = mediaFiles.replace(
//                                { it.uri == currentValue.image.uri },
//                                mediaFile
//                            )
//                        )
//                    },
//                    factory = {
//                        UserAttachedMediaFiles(checkId, listOf(mediaFile))
//                    }
//                )
//            )
//        }
        navigator.navigateBack()
    }

    private fun selectDrawingMode() {
        setState {
            copy(
                currentOperation = DrawingOperation.LineDrawing(),
                drawingColors = DrawingColors(
                    lineDrawingColors,
                    lineDrawingColors.first()
                )
            )
        }
    }

    private fun selectTextMode() {
        stopLineDrawing()
        setState {
            copy(
                currentOperation = DrawingOperation.TextDrawing(),
                drawingColors = DrawingColors(
                    textDrawingColors,
                    textDrawingColors.first()
                )
            )
        }
    }

    private fun selectColor(color: Int) {
        setState { copy(drawingColors = drawingColors.copy(selectedColor = color)) }
        (currentValue.currentOperation as? DrawingOperation.TextDrawing)?.text?.let { text ->
            setState {
                copy(currentOperation = DrawingOperation.TextDrawing(text.copy(color = color)))
            }
        }
    }

    private fun selectFontSize(size: Int) {
        setState { copy(fontSize = fontSize.copy(selectedSize = size)) }
        (currentValue.currentOperation as? DrawingOperation.TextDrawing)?.text?.let { text ->
            setState {
                copy(currentOperation = DrawingOperation.TextDrawing(text.copy(fontSize = size)))
            }
        }
    }

    private fun startLineDrawing(point: Point) {
        val path = Path()
        path.moveTo(point.x, point.y)
        setState {
            copy(
                currentOperation = DrawingOperation.LineDrawing(
                    Drawing.Line(
                        path = path,
                        points = emptyList(),
                        color = currentValue.drawingColors.selectedColor
                    )
                )
            )
        }
    }

    private fun moveLineDrawing(point: Point) {
        val currentLine =
            (currentValue.currentOperation as? DrawingOperation.LineDrawing)?.line ?: return
        currentLine.path.lineTo(point.x, point.y)
        setState {
            copy(
                currentOperation = DrawingOperation.LineDrawing(
                    currentLine.copy(points = currentLine.points + point)
                )
            )
        }
    }

    private fun stopLineDrawing() {
        val currentLine =
            (currentValue.currentOperation as? DrawingOperation.LineDrawing)?.line ?: return
        setState {
            copy(
                drawings = drawings + currentLine,
                currentOperation = DrawingOperation.LineDrawing()
            )
        }
    }

    private fun undo() {
        // nothing to undo
        if (currentValue.drawings.isEmpty()) return
        setState {
            copy(drawings = drawings - drawings.last())
        }
    }

    private fun clearEditing() {
        // nothing to clear
        if (currentValue.drawings.isEmpty()) return
        setState {
            copy(drawings = emptyList())
        }
    }
}
