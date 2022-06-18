package com.gvoltr.compose_image_editor.capture

import android.net.Uri

sealed interface MediaCaptureEffect {
    data class TakePicture(val fileUri: Uri) : MediaCaptureEffect
    data class StartVideoCapture(val fileUri: Uri, val recordAudio: Boolean) :
        MediaCaptureEffect
    object StopVideoCapture : MediaCaptureEffect
    object RequestPermissions : MediaCaptureEffect
}