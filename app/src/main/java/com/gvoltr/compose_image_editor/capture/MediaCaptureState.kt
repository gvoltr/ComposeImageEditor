package com.gvoltr.compose_image_editor.capture

import android.net.Uri
import com.gvoltr.compose_image_editor.base.State
import com.gvoltr.compose_image_editor.capture.camera.CameraType
import com.gvoltr.compose_image_editor.media.LocalFile

data class MediaCaptureState(
    val activeMedia: List<LocalFile> = emptyList(),
    val cameraType: CameraType = CameraType.Back,
    val activeCaptureState: CaptureState = CaptureState.Photo,
    val cameraPermissionGranted: Boolean = false,
    val recordAudioPermissionGranted: Boolean = false,
    val isLoading: Boolean = false
) : State {

    companion object {
        val Empty = MediaCaptureState()
    }
}

sealed class RecordingState {
    /**
     * Recording is not active and can be started
     */
    object Inactive : RecordingState()

    /**
     * Recording is in progress
     */
    data class Active(val uri: Uri) : RecordingState()

    /**
     * End recording is triggered and VM is waiting for the result of record operation
     * record button should not be active when recording in this state
     */
    data class Finalization(val uri: Uri) : RecordingState()
}

sealed class CaptureState {
    object Photo : CaptureState()
    data class Video(val recordingState: RecordingState) : CaptureState()
}
