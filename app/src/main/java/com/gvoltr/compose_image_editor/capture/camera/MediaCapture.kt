package com.gvoltr.compose_image_editor.capture.camera

import android.net.Uri

/**
 * Interface for providing a binding between internal camera capture functions and outer composable
 */
interface MediaCapture {
    suspend fun takePicture(fileUri: Uri): MediaCaptureResult
    suspend fun startVideoCapture(fileUri: Uri, recordAudio: Boolean)
    suspend fun stopVideoCapture(): MediaCaptureResult

    companion object {
        val stub = object : MediaCapture {
            override suspend fun takePicture(fileUri: Uri): MediaCaptureResult {
                throw IllegalStateException("Stub media capture can't be used for real capture")
            }

            override suspend fun startVideoCapture(fileUri: Uri, recordAudio: Boolean) {
                throw IllegalStateException("Stub media capture can't be used for real capture")
            }

            override suspend fun stopVideoCapture(): MediaCaptureResult {
                throw IllegalStateException("Stub media capture can't be used for real capture")
            }
        }
    }
}