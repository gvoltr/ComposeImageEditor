package com.gvoltr.compose_image_editor.capture

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.gvoltr.compose_image_editor.base.BaseViewModel
import com.gvoltr.compose_image_editor.capture.camera.CameraType
import com.gvoltr.compose_image_editor.media.FileType
import com.gvoltr.compose_image_editor.media.LocalFile
import com.gvoltr.compose_image_editor.media.file.FileInfoProvider
import com.gvoltr.compose_image_editor.media.file.FileStorage
import com.gvoltr.compose_image_editor.state.MediaStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaCaptureViewModel @Inject constructor(
    private val fileStorage: FileStorage,
    private val mediaState: MediaStateHolder,
    private val fileInfoProvider: FileInfoProvider
) : BaseViewModel<MediaCaptureState, MediaCaptureAction, MediaCaptureEffect>(MediaCaptureState.Empty) {

    private val logTag = "MediaCaptureVM"

    init {
        viewModelScope.launch {
            mediaState.currentState.collectLatest { mediaState ->
                setState {
                    copy(activeMedia = mediaState.capturedMedia)
                }
            }
        }
    }

    override suspend fun onAction(action: MediaCaptureAction) {
        when (action) {
            is MediaCaptureAction.OnCameraPermissionStatusChanged -> {
                setState { copy(cameraPermissionGranted = action.granted) }
            }
            is MediaCaptureAction.OnRecordAudioPermissionStatusChanged ->  {
                setState { copy(recordAudioPermissionGranted = action.granted) }
            }
            is MediaCaptureAction.OnGalleryMediaSelected -> addMedia(action.uri)
            is MediaCaptureAction.OnPictureTaken -> addMedia(action.uri, FileType.Image)
            MediaCaptureAction.OnTakePictureFailure -> Unit
            MediaCaptureAction.OnVideoCaptureFailure -> onVideoRecordingFailure()
            MediaCaptureAction.OnVideoCaptureStopped -> onVideoRecordingStopped()
            is MediaCaptureAction.OpenMedia -> TODO()
            MediaCaptureAction.StartVideoCapture -> startVideoRecording()
            MediaCaptureAction.StopVideoCapture -> stopVideoRecording()
            MediaCaptureAction.SwapCamera -> swapCamera()
            MediaCaptureAction.SwitchToPhotoCapture -> setState {
                copy(activeCaptureState = CaptureState.Photo)
            }
            MediaCaptureAction.SwitchToVideoCapture -> setState {
                copy(activeCaptureState = CaptureState.Video(RecordingState.Inactive))
            }
            MediaCaptureAction.TakePicture -> takePicture()
        }
    }

    private suspend fun takePicture() {
        if (!currentValue.cameraPermissionGranted) {
            emitEffect(MediaCaptureEffect.RequestPermissions)
            return
        }

        emitEffect(MediaCaptureEffect.TakePicture(fileStorage.createTempImageFile()))
    }

    private suspend fun startVideoRecording() {
        if (!currentValue.cameraPermissionGranted) {
            emitEffect(MediaCaptureEffect.RequestPermissions)
            return
        }

        val recordingState =
            (currentValue.activeCaptureState as? CaptureState.Video)?.recordingState
        // Do not start recording if recording is already started
        if (recordingState is RecordingState.Active) return

        val videoUri = fileStorage.createTempVideoFile()
        emitEffect(
            MediaCaptureEffect.StartVideoCapture(
                videoUri,
                currentValue.recordAudioPermissionGranted
            )
        )
        setState {
            copy(activeCaptureState = CaptureState.Video(RecordingState.Active(videoUri)))
        }
    }

    private fun stopVideoRecording() {
        val recordingState =
            (currentValue.activeCaptureState as? CaptureState.Video)?.recordingState
        if (recordingState !is RecordingState.Active) {
            Log.e(logTag, "Can't stop video recording state is not Active")
            return
        }

        setState {
            copy(activeCaptureState = CaptureState.Video(RecordingState.Finalization(recordingState.uri)))
        }
        emitEffect(MediaCaptureEffect.StopVideoCapture)
    }

    private fun onVideoRecordingStopped() {
        val recordingState =
            (currentValue.activeCaptureState as? CaptureState.Video)?.recordingState
        if (recordingState !is RecordingState.Finalization) {
            Log.e(logTag, "Can't stop video recording state is not Active")
            return
        }

        addMedia(recordingState.uri, fileType = FileType.Video)
        setState {
            copy(
                activeCaptureState = CaptureState.Video(RecordingState.Inactive)
            )
        }
    }

    private fun onVideoRecordingFailure() {
        setState {
            copy(
                activeCaptureState = CaptureState.Video(RecordingState.Inactive)
            )
        }
    }

    private fun swapCamera() {
        if (!currentValue.cameraPermissionGranted) return
        setState {
            copy(
                cameraType = if (currentValue.cameraType == CameraType.Back) {
                    CameraType.Front
                } else {
                    CameraType.Back
                }
            )
        }
    }

    private fun addMedia(uri: Uri, fileType: FileType? = null) {
        mediaState.setState {
            copy(
                capturedMedia = capturedMedia + createMediaFile(uri, fileType)
            )
        }
    }

    private fun createMediaFile(
        uri: Uri,
        type: FileType? = null
    ): LocalFile {
        val fileType = type ?: fileInfoProvider.getFileType(uri)
        val duration =
            if (fileType == FileType.Video) fileInfoProvider.getVideoLength(uri) else null
        return LocalFile(
            uri = uri,
            uriType = fileInfoProvider.getUriType(uri),
            fileType = fileType,
            duration = duration
        )
    }
}