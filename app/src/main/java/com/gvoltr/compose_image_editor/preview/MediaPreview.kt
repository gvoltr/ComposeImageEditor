package com.gvoltr.compose_image_editor.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ExoPlayer
import com.gvoltr.compose_image_editor.media.FileType
import com.gvoltr.compose_image_editor.media.LocalFile
import com.gvoltr.compose_image_editor.media.MediaFile
import com.gvoltr.compose_image_editor.ui.theme.Colors
import com.shopmonkey.shopmonkeyapp.common.components.appbar.child.ChildScreenAppBar
import com.shopmonkey.shopmonkeyapp.common.components.appbar.child.NavigationType
import java.lang.Integer.min

@Composable
fun MediaPreviewScreen(
    viewModel: MediaPreviewViewModel = hiltViewModel()
) {
    val state by viewModel.currentState.collectAsState()
    MediaPreviewContent(state = state, actioner = viewModel::submitAction)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MediaPreviewContent(
    state: MediaPreviewState,
    actioner: (MediaPreviewAction) -> Unit
) {
    val pagerState = rememberPagerState()
    val exoWrapper = remember { mutableStateOf(HashMap<Int, ExoPlayer>()) }
    val previousPage = remember { mutableStateOf(0) }
    val lifeCycleState = LocalLifecycleOwner.current.lifecycle.observeAsSate()

    // for initial scroll to the user selected image
    LaunchedEffect(key1 = state.selectedMediaPosition) {
        pagerState.scrollToPage(state.selectedMediaPosition)
    }

    // Pause player when moving between pages
    LaunchedEffect(key1 = pagerState.currentPage) {
        val player = exoWrapper.value[previousPage.value]
        if (player != null && player.isPlaying) {
            player.pause()
        }
        previousPage.value = pagerState.currentPage
    }

    // Pause player when ON_PAUSE happen
    LaunchedEffect(key1 = lifeCycleState.value) {
        if (lifeCycleState.value == Lifecycle.Event.ON_PAUSE) {
            val player = exoWrapper.value[pagerState.currentPage]
            if (player != null && player.isPlaying) {
                player.pause()
            }
        }
    }
    if (state.media.isNotEmpty()) {
        Column(Modifier.fillMaxSize()) {
            // after last media item deletion current page will be equal to size
            val selectedMedia = state.media[min(pagerState.currentPage, state.media.size - 1)]
            AppBar(
                selectedMedia = selectedMedia,
                actioner = actioner
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Colors.Black)
            ) {
                HorizontalPager(
                    count = state.media.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val media = state.media[page]

                    if (media.fileType == FileType.Video) {
                        VideoPlayer(
                            videoUri = media.path,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            exoWrapper.value[page] = it
                        }
                    } else {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberImagePainter(
                                media.path,
                                builder = {
                                    crossfade(true)
                                }
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    activeColor = Colors.White,
                    inactiveColor = Colors.Gray300,
                    indicatorWidth = 12.dp,
                    spacing = 10.dp
                )
            }

            Controls(
                selectedMedia = selectedMedia,
                actioner = actioner,
                exoPlayer = exoWrapper.value[pagerState.currentPage]
            )
        }
    }
}

@Composable
private fun AppBar(
    selectedMedia: MediaFile?,
    actioner: (MediaPreviewAction) -> Unit
) {
    ChildScreenAppBar(
        title = if (selectedMedia?.fileType == FileType.Image) {
            "View Photo"
        } else {
            "View Video"
        },
        backgroundColor = Colors.Gray900,
        contentColor = Colors.White,
        navigationType = NavigationType.GoBack,
        actions = {
            if (selectedMedia != null && selectedMedia is LocalFile) {
                Text(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                actioner(MediaPreviewAction.DeleteMedia(selectedMedia))
                            }
                        )
                        .padding(14.dp),
                    text = "Delete",
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.W500,
                        color = Colors.White
                    ),
                    color = Colors.White
                )
            }
        }
    )
}

@Composable
private fun Controls(
    selectedMedia: MediaFile,
    actioner: (MediaPreviewAction) -> Unit,
    exoPlayer: ExoPlayer?
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Colors.Gray900)
    ) {
        if (selectedMedia.fileType == FileType.Image) {
            if (selectedMedia is LocalFile) {
                Text(
                    modifier = Modifier
                        .clickable { actioner(MediaPreviewAction.EditPhoto(selectedMedia)) }
                        .align(Alignment.Center),
                    text = "Edit Photo",
                    style = MaterialTheme.typography.h4,
                    color = Colors.White
                )
            }
        } else {
            // add video controls
            if (exoPlayer != null) {
                VideoControls(
                    exoPlayer = exoPlayer,
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.Center)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun Lifecycle.observeAsSate(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsSate.addObserver(observer)
        onDispose {
            this@observeAsSate.removeObserver(observer)
        }
    }
    return state
}
