package com.gvoltr.compose_image_editor.capture

import android.net.Uri
import com.gvoltr.compose_image_editor.media.LocalFile

sealed interface MediaCaptureAction {
    object SwitchToPhotoCapture : MediaCaptureAction
    object SwitchToVideoCapture : MediaCaptureAction
    object TakePicture : MediaCaptureAction
    object StartVideoCapture : MediaCaptureAction
    object StopVideoCapture : MediaCaptureAction
    object OnVideoCaptureStopped : MediaCaptureAction
    object OnVideoCaptureFailure : MediaCaptureAction
    data class OnCameraPermissionStatusChanged(val granted: Boolean) : MediaCaptureAction
    data class OnRecordAudioPermissionStatusChanged(val granted: Boolean) : MediaCaptureAction
    data class OnPictureTaken(val uri: Uri) : MediaCaptureAction
    data class OnGalleryMediaSelected(val uri: Uri) : MediaCaptureAction
    object OnTakePictureFailure : MediaCaptureAction
    object SwapCamera : MediaCaptureAction
    data class OpenMedia(val mediaFile: LocalFile) : MediaCaptureAction
}