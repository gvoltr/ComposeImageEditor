package com.gvoltr.compose_image_editor.preview

import com.gvoltr.compose_image_editor.base.State
import com.gvoltr.compose_image_editor.media.MediaFile

data class MediaPreviewState(
    val media: List<MediaFile> = emptyList(),
    val selectedMediaPosition: Int = 0
) : State {
    companion object {
        val Empty = MediaPreviewState()
    }
}
