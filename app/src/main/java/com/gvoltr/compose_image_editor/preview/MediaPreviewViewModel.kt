package com.gvoltr.compose_image_editor.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gvoltr.compose_image_editor.base.BaseViewModel
import com.gvoltr.compose_image_editor.base.takeWhenChanged
import com.gvoltr.compose_image_editor.draw.PhotoEditorDestination
import com.gvoltr.compose_image_editor.media.LocalFile
import com.gvoltr.compose_image_editor.media.file.FileInfoProvider
import com.gvoltr.compose_image_editor.media.file.FileStorage
import com.gvoltr.compose_image_editor.navigation.Navigator
import com.gvoltr.compose_image_editor.state.MediaStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class MediaPreviewViewModel @Inject constructor(
    private val fileInfoProvider: FileInfoProvider,
    private val fileStorage: FileStorage,
    private val mediaState: MediaStateHolder,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<MediaPreviewState, MediaPreviewAction, Unit>(MediaPreviewState.Empty) {

    init {
        // fill the initial state of media files with position of selected media
        setupMediaWithPosition()
        viewModelScope.launch {
            // subscribe to all the media changes
            subscribeToMediaState()
        }
    }

    override suspend fun onAction(action: MediaPreviewAction) {
        when (action) {
            is MediaPreviewAction.DeleteMedia -> deleteMedia(action.mediaFile)
            is MediaPreviewAction.EditPhoto -> {
                navigator.navigate(
                    PhotoEditorDestination.createCommand(action.mediaFile.uri.lastPathSegment ?: "")
                )
            }
        }
    }

    // One time media and selected media position set
    private fun setupMediaWithPosition() {
        val selectedMediaFilename =
            savedStateHandle.get<String>(MediaPreviewDestination.argSelectedMedia).orEmpty()
        val media = mediaState.currentValue.capturedMedia
        val position =
            max(media.indexOfFirst { it.uri.lastPathSegment == selectedMediaFilename }, 0)
        setState { copy(media = media, selectedMediaPosition = position) }
    }

    private suspend fun subscribeToMediaState() {
        mediaState.currentState
            .takeWhenChanged { it.capturedMedia }
            .collectLatest {
                if (it.isEmpty()) {
                    // navigate back if there is no media to show
                    navigator.navigateBack()
                } else {
                    setState {
                        copy(
                            media = it,
                            selectedMediaPosition = min(selectedMediaPosition, it.lastIndex)
                        )
                    }
                }
            }
    }

    private suspend fun deleteMedia(media: LocalFile) {
        if (fileInfoProvider.isFromAppCache(media.uri)) {
            fileStorage.delete(media.uri)
        }

        mediaState.setState {
            copy(
                capturedMedia = capturedMedia - media
            )
        }
    }
}
