package com.gvoltr.compose_image_editor.capture

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gvoltr.compose_image_editor.ui.theme.Colors

@Composable
internal fun Controls(
    modifier: Modifier,
    state: MediaCaptureState,
    actioner: (MediaCaptureAction) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val buttonModifier = Modifier
            .padding(top = 12.dp)
            .size(68.dp)
            .align(Alignment.TopCenter)

        when (state.activeCaptureState) {
            is CaptureState.Photo -> TakePictureButton(
                modifier = buttonModifier,
                actioner = actioner
            )
            is CaptureState.Video -> TakeVideoButton(
                modifier = buttonModifier,
                state = state.activeCaptureState,
                actioner = actioner
            )
        }

        val recordingInProgress = state.activeCaptureState is CaptureState.Video &&
                state.activeCaptureState.recordingState !is RecordingState.Inactive
        val controlsAlpha by animateFloatAsState(targetValue = if (recordingInProgress) 0.5f else 1f)

        val swapCameraInteractionSource = remember { MutableInteractionSource() }
        Box(
            Modifier
                .padding(top = 22.dp, start = 36.dp)
                .size(48.dp)
                .align(Alignment.TopStart)
                .clickable(
                    interactionSource = swapCameraInteractionSource,
                    indication = rememberRipple(bounded = false, color = Colors.White),
                    onClick = { actioner(MediaCaptureAction.SwapCamera) },
                    enabled = !recordingInProgress
                )
                .alpha(controlsAlpha)
                .border(width = 1.5.dp, color = Colors.White, shape = CircleShape)

        ) {
            Icon(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.Center),
                imageVector = Icons.Filled.SwapHoriz,
                contentDescription = "",
                tint = Colors.White
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                uri?.let { actioner(MediaCaptureAction.OnGalleryMediaSelected(it)) }
            }
        )

        val galleryInteractionSource = remember { MutableInteractionSource() }
        Box(
            Modifier
                .padding(top = 22.dp, end = 36.dp)
                .size(48.dp)
                .align(Alignment.TopEnd)
                .clickable(
                    interactionSource = galleryInteractionSource,
                    indication = rememberRipple(bounded = false, color = Colors.White),
                    onClick = { launcher.launch("image/* video/*") },
                    enabled = !recordingInProgress
                )
                .alpha(controlsAlpha)
                .border(width = 1.5.dp, color = Colors.White, shape = CircleShape)
        ) {
            Icon(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.Center),
                imageVector = Icons.Filled.Storage,
                contentDescription = "",
                tint = Colors.White
            )
        }

        if (!recordingInProgress) {
            CameraSwitch(
                modifier = Modifier.align(Alignment.BottomCenter),
                state = state,
                actioner = actioner
            )
        }
    }
}

@Composable
private fun TakePictureButton(
    modifier: Modifier,
    actioner: (MediaCaptureAction) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, color = Colors.White),
                onClick = { actioner(MediaCaptureAction.TakePicture) }
            )
            .border(width = 1.5.dp, color = Colors.White, shape = CircleShape)
    ) {
        Box(
            Modifier
                .size(36.dp)
                .align(Alignment.Center)
                .background(color = Colors.White, shape = CircleShape)
        )
    }
}

@Composable
private fun TakeVideoButton(
    modifier: Modifier,
    state: CaptureState.Video,
    actioner: (MediaCaptureAction) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val recordingInProgress = state.recordingState is RecordingState.Active
    Box(
        modifier
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, color = Colors.White),
                onClick = {
                    if (recordingInProgress) {
                        actioner(MediaCaptureAction.StopVideoCapture)
                    } else {
                        actioner(MediaCaptureAction.StartVideoCapture)
                    }
                },
                // Disable button press while finalization is happening
                // and we can't start/stop another recording yet
                enabled = state.recordingState !is RecordingState.Finalization
            )
            .border(width = 1.5.dp, color = Colors.White, shape = CircleShape)
    ) {
        val corners by animateIntAsState(
            targetValue = if (recordingInProgress) 12 else 50,
            animationSpec = tween(300)
        )
        val size by animateIntAsState(
            targetValue = if (recordingInProgress) 32 else 36,
            animationSpec = tween(150)
        )
        Box(
            Modifier
                .size(size.dp)
                .align(Alignment.Center)
                .background(color = Colors.Red600, shape = RoundedCornerShape(corners))
        )
    }
}

@Composable
private fun CameraSwitch(
    modifier: Modifier,
    state: MediaCaptureState,
    actioner: (MediaCaptureAction) -> Unit
) {
    Box(
        modifier
            .padding(bottom = 20.dp)
            .height(32.dp)
            .width(238.dp)
    ) {
        val capturePhotoActive = state.activeCaptureState == CaptureState.Photo
        val dynamicPadding by animateDpAsState(
            targetValue = if (capturePhotoActive) 83.dp else 0.dp,
            animationSpec = tween(300)
        )
        Row(
            Modifier
                .fillMaxHeight()
                .padding(start = dynamicPadding)
        ) {
            val photoTextColor by animateColorAsState(
                targetValue = if (capturePhotoActive) Colors.Gray900 else Colors.White,
                animationSpec = tween(300)
            )
            val photoBackgroundColor by animateColorAsState(
                targetValue = if (capturePhotoActive) Colors.White else Color.Transparent,
                animationSpec = tween(300)
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(71.dp)
                    .background(photoBackgroundColor, shape = RoundedCornerShape(50))
                    .clickable(
                        onClick = { actioner(MediaCaptureAction.SwitchToPhotoCapture) },
                        enabled = !capturePhotoActive
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Photo",
                    style = MaterialTheme.typography.subtitle1,
                    color = photoTextColor
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            val videoTextColor by animateColorAsState(
                targetValue = if (!capturePhotoActive) Colors.Gray900 else Colors.White,
                animationSpec = tween(300)
            )
            val videoBackgroundColor by animateColorAsState(
                targetValue = if (!capturePhotoActive) Colors.White else Color.Transparent,
                animationSpec = tween(300)
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(71.dp)
                    .background(videoBackgroundColor, shape = RoundedCornerShape(50))
                    .clickable(
                        onClick = { actioner(MediaCaptureAction.SwitchToVideoCapture) },
                        enabled = capturePhotoActive
                    )
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Video",
                    style = MaterialTheme.typography.subtitle1,
                    color = videoTextColor
                )
            }
        }
    }
}
