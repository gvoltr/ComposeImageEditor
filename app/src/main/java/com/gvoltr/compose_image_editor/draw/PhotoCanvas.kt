package com.gvoltr.compose_image_editor.draw

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
internal fun PhotoCanvas(
    modifier: Modifier,
    state: PhotoEditorState,
    actioner: (PhotoEditorAction) -> Unit
) {
    val parentSize = remember { mutableStateOf(IntSize.Zero) }
    val canvasSize = remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(key1 = canvasSize.value) {
        actioner(
            PhotoEditorAction.CanvasSizeChange(
                canvasSize.value.width.toFloat(),
                canvasSize.value.height.toFloat()
            )
        )
    }

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .onSizeChanged {
                parentSize.value = it
            }
    ) {
        val imageWidthDominates = remember {
            derivedStateOf {
                val parentAspectRation =
                    parentSize.value.width.toFloat() / parentSize.value.height.toFloat()
                 parentAspectRation < state.image.aspectRatio
            }
        }

        val sizeModifier = if (imageWidthDominates.value) {
            val widthInDp = with(LocalDensity.current) {
                parentSize.value.width.toDp()
            }
            Modifier
                .width(widthInDp)
                .height(widthInDp.div(state.image.aspectRatio))
        } else {
            val heightInDp = with(LocalDensity.current) {
                parentSize.value.height.toDp()
            }
            Modifier
                .height(heightInDp)
                .width(heightInDp.times(state.image.aspectRatio))
        }
            .align(Alignment.Center)
            .clip(RoundedCornerShape(4.dp))
            .onSizeChanged {
                canvasSize.value = it
            }
        Image(
            modifier = sizeModifier,
            painter = rememberImagePainter(state.image.uri),
            contentDescription = "image",
            contentScale = if (imageWidthDominates.value) ContentScale.FillWidth else ContentScale.FillHeight
        )
        DrawingLayer(
            modifier = sizeModifier,
            state = state,
            actioner = actioner
        )
    }
}

