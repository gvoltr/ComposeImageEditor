package com.gvoltr.compose_image_editor.draw

sealed class PhotoEditorAction {
    // Controls
    object Save : PhotoEditorAction()
    object Undo : PhotoEditorAction()
    object ClearEditing : PhotoEditorAction()
    object SelectDrawingMode : PhotoEditorAction()
    object SelectTextMode : PhotoEditorAction()
    data class SelectColor(val color: Int) : PhotoEditorAction()
    data class SelectFontSize(val fontSize: Int) : PhotoEditorAction()

    // Line drawing
    data class CanvasSizeChange(val width: Float, val height: Float) : PhotoEditorAction()
    data class StartLineDrawing(val point: Point) : PhotoEditorAction()
    data class MoveLineDrawing(val point: Point) : PhotoEditorAction()
    object StopLineDrawing : PhotoEditorAction()
}
