package com.gvoltr.compose_image_editor.preview

import com.gvoltr.compose_image_editor.media.LocalFile

sealed class MediaPreviewAction {
    data class DeleteMedia(val mediaFile: LocalFile) : MediaPreviewAction()
    data class EditPhoto(val mediaFile: LocalFile) : MediaPreviewAction()
}
