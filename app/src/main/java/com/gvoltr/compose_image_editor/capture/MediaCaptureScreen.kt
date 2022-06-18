package com.gvoltr.compose_image_editor.capture

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.gvoltr.compose_image_editor.capture.camera.CameraView
import com.gvoltr.compose_image_editor.capture.camera.MediaCapture
import com.gvoltr.compose_image_editor.capture.camera.MediaCaptureResult
import com.gvoltr.compose_image_editor.common.components.permission.PermissionNotAvailableDefaultContent
import com.gvoltr.compose_image_editor.media.FileType
import com.gvoltr.compose_image_editor.ui.theme.Colors
import com.shopmonkey.shopmonkeyapp.common.components.appbar.child.ChildScreenAppBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MediaCaptureScreen(
    viewModel: MediaCaptureViewModel = hiltViewModel()
) {
    val state by viewModel.currentState.collectAsState()
    MediaCameraContent(
        state = state,
        effects = viewModel.effects,
        actioner = viewModel::submitAction
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MediaCameraContent(
    state: MediaCaptureState,
    effects: Flow<MediaCaptureEffect>,
    actioner: (MediaCaptureAction) -> Unit
) {
    var mediaCapture by remember { mutableStateOf(MediaCapture.stub) }

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    permissionState.permissions.forEach { permission ->
        LaunchedEffect(key1 = permission.hasPermission) {
            when (permission.permission) {
                Manifest.permission.CAMERA ->
                    actioner(MediaCaptureAction.OnCameraPermissionStatusChanged(permission.hasPermission))
                Manifest.permission.RECORD_AUDIO ->
                    actioner(MediaCaptureAction.OnRecordAudioPermissionStatusChanged(permission.hasPermission))
            }
        }
    }

    LaunchedEffect(permissionState) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(effects) {
        effects.collectLatest { effect ->
            when (effect) {
                is MediaCaptureEffect.TakePicture -> {
                    val result = mediaCapture.takePicture(effect.fileUri)
                    if (result == MediaCaptureResult.Success) {
                        actioner(MediaCaptureAction.OnPictureTaken(effect.fileUri))
                    } else {
                        actioner(MediaCaptureAction.OnTakePictureFailure)
                    }
                }
                is MediaCaptureEffect.StartVideoCapture -> {
                    mediaCapture.startVideoCapture(effect.fileUri, effect.recordAudio)
                }
                MediaCaptureEffect.StopVideoCapture -> {
                    val result = mediaCapture.stopVideoCapture()
                    if (result == MediaCaptureResult.Success) {
                        actioner(MediaCaptureAction.OnVideoCaptureStopped)
                    } else {
                        actioner(MediaCaptureAction.OnVideoCaptureFailure)
                    }
                }
                MediaCaptureEffect.RequestPermissions -> {
                    permissionState.launchMultiplePermissionRequest()
                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ChildScreenAppBar(
            title = "Take a picture",
            backgroundColor = Colors.Gray900,
            contentColor = Colors.White
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (state.cameraPermissionGranted) {
                CameraView(
                    modifier = Modifier.fillMaxSize(),
                    cameraType = state.cameraType,
                    onCameraReady = {
                        mediaCapture = it
                    }
                )
            } else {
                PermissionNotAvailableDefaultContent(
                    message = "Please grant permission to use camera",
                    buttonTitle = "Grant permission",
                    isButtonEnabled = true
                ) {
                    permissionState.launchMultiplePermissionRequest()
                }
            }
            SavedMedia(modifier = Modifier.align(Alignment.BottomCenter), state = state, actioner = actioner)
        }
        Divider(color = Colors.Gray700)
        Controls(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .background(Colors.Gray900),
            state = state,
            actioner = actioner
        )
    }
}

@Composable
private fun SavedMedia(
    modifier: Modifier,
    state: MediaCaptureState,
    actioner: (MediaCaptureAction) -> Unit
) {
    if (state.activeMedia.isNotEmpty()) {
        val context = LocalContext.current
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp)
                .horizontalScroll(rememberScrollState())
                .background(Colors.Gray900.copy(alpha = 0.8f))
                .padding(horizontal = 10.dp),
        ) {
            state.activeMedia.forEach {
                Box(
                    Modifier
                        .padding(4.dp)
                        .size(50.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { actioner(MediaCaptureAction.OpenMedia(it)) }
                ) {

                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentDescription = "Captured image",
                        contentScale = ContentScale.FillWidth,
                        model = ImageRequest.Builder(context = context)
                            .data(it.uri)
                            .crossfade(true)
                            .apply {
                                if (it.fileType == FileType.Video) decoderFactory(VideoFrameDecoder.Factory())
                            }
                            .build()
                    )
                    if (it.fileType == FileType.Video) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .align(Alignment.BottomCenter)
                                .background(Colors.Gray900.copy(alpha = 0.8f)),
                            text = it.duration ?: "",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                            color = Colors.White
                        )
                    }
                }
            }
        }
    }
}
