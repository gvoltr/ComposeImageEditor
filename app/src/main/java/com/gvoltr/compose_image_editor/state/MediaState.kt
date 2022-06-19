package com.gvoltr.compose_image_editor.state

import com.gvoltr.compose_image_editor.base.FeatureState
import com.gvoltr.compose_image_editor.base.FeatureStateHolder
import com.gvoltr.compose_image_editor.media.LocalFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStateHolder @Inject constructor() : FeatureStateHolder<MediaState>(MediaState.Empty)

data class MediaState(
    val capturedMedia: List<LocalFile> = emptyList()
) : FeatureState {
    companion object {
        val Empty = MediaState()
    }
}