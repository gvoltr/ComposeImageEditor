package com.gvoltr.compose_image_editor.capture.camera

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.util.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    cameraType: CameraType,
    onCameraReady: (MediaCapture) -> Unit = {},
) {
    Box(modifier = modifier) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var previewUseCase by remember { mutableStateOf(Preview.Builder().build()) }

        val imageCapture by remember {
            mutableStateOf(
                ImageCapture.Builder()
                    .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
            )
        }
        val mediaCapture by remember { mutableStateOf(createMediaCapture(context, imageCapture)) }

        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onPreview = {
                previewUseCase = it
            }
        )

        LaunchedEffect(previewUseCase, imageCapture, cameraType) {
            val cameraProvider = context.getCameraProvider()
            val cameraSelector = when (cameraType) {
                CameraType.Back -> CameraSelector.DEFAULT_BACK_CAMERA
                CameraType.Front -> CameraSelector.DEFAULT_FRONT_CAMERA
            }
            try {
                // Must unbind the use-cases before rebinding them.
                cameraProvider.unbindAll()
                // Can't reuse video capture in the same way as image capture for some reason.
                // reusing will crash app.
                val videoCaptureUseCase = getVideoCapture()
                mediaCapture.videoCaptureUseCase = videoCaptureUseCase
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    previewUseCase,
                    videoCaptureUseCase,
                    imageCapture,
                )
                onCameraReady(mediaCapture)
            } catch (ex: Exception) {
                Log.e("CameraCapture", "Failed to bind camera use cases", ex)
            }
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onPreview: (Preview) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            onPreview(
                Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
            )
            previewView
        }
    )
}

private fun getVideoCapture(): VideoCapture<Recorder> {
    val recorder = Recorder.Builder()
        .setQualitySelector(getQualitySelector())
        .build()
    return VideoCapture.withOutput(recorder)
}

private fun getQualitySelector() = QualitySelector
    .fromOrderedList(listOf(Quality.FHD, Quality.HD, Quality.UHD, Quality.SD, Quality.LOWEST))

private fun createMediaCapture(
    context: Context,
    imageCaptureUseCase: ImageCapture
) = object : MediaCapture {
    var videoCaptureUseCase: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var stopVideoCaptureContinuation: Continuation<MediaCaptureResult>? = null

    private val videoCaptureListener = Consumer<VideoRecordEvent> {
        if (it is VideoRecordEvent.Finalize) {
            val result = if (it.error == VideoRecordEvent.Finalize.ERROR_NONE) {
                MediaCaptureResult.Success
            } else {
                MediaCaptureResult.Failure
            }
            stopVideoCaptureContinuation?.resume(result)
            stopVideoCaptureContinuation = null
        }
    }

    override suspend fun takePicture(fileUri: Uri): MediaCaptureResult {
        return imageCaptureUseCase.takePicture(fileUri, context.executor)
    }

    @SuppressLint("MissingPermission")
    override suspend fun startVideoCapture(fileUri: Uri, recordAudio: Boolean) {
        val output = FileOutputOptions.Builder(fileUri.toFile()).build()

        var recording = videoCaptureUseCase?.output?.prepareRecording(context, output)
        if (recordAudio) {
            recording = recording?.withAudioEnabled()
        }

        activeRecording = recording?.start(context.executor, videoCaptureListener)
    }

    override suspend fun stopVideoCapture(): MediaCaptureResult {
        activeRecording?.stop()
        activeRecording = null
        return suspendCoroutine {
            stopVideoCaptureContinuation = it
        }
    }
}

private suspend fun ImageCapture.takePicture(fileUri: Uri, executor: Executor): MediaCaptureResult {
    val photoFile = withContext(Dispatchers.IO) {
        fileUri.toFile()
    }

    return suspendCoroutine { continuation ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("TakePicture", "onImageSaved")
                    continuation.resume(MediaCaptureResult.Success)
                }

                override fun onError(ex: ImageCaptureException) {
                    Log.e("TakePicture", "Image capture failed", ex)
                    continuation.resume(MediaCaptureResult.Failure)
                }
            }
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, executor)
        }
    }

private val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)
