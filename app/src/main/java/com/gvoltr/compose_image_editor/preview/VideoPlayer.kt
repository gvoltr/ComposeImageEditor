package com.gvoltr.compose_image_editor.preview

import android.graphics.Color
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayer(
    videoUri: String,
    modifier: Modifier = Modifier,
    onExoReady: (ExoPlayer) -> Unit
) {
    val context = LocalContext.current

    // create our player
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            onExoReady(this)
        }
    }

    LaunchedEffect(key1 = videoUri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUri))
        exoPlayer.prepare()
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            // exo player view for our video player
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
}

@Composable
fun VideoControls(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            PlayerControlView(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                player = exoPlayer
                showTimeoutMs = 0
                setShowNextButton(false)
                setShowPreviousButton(false)

                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = {
            it.player = exoPlayer
        }
    )
}
