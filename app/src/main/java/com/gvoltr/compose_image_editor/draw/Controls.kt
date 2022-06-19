package com.gvoltr.compose_image_editor.draw

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gvoltr.compose_image_editor.ui.theme.Colors

@Composable
fun Controls(
    state: PhotoEditorState,
    actioner: (PhotoEditorAction) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(horizontal = 26.dp)
        ) {
            val undoInteractionSource = remember { MutableInteractionSource() }
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(
                        interactionSource = undoInteractionSource,
                        indication = rememberRipple(bounded = false, color = Colors.White),
                        onClick = { actioner(PhotoEditorAction.Undo) },
                    )
                    .padding(4.dp),
                imageVector = Icons.Filled.Undo,
                contentDescription = "Undo",
                tint = Colors.White
            )

            StyleSelector(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = state,
                actioner = actioner
            )

            val clearEditingInteractionSource = remember { MutableInteractionSource() }
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(
                        interactionSource = clearEditingInteractionSource,
                        indication = rememberRipple(bounded = false, color = Colors.White),
                        onClick = { actioner(PhotoEditorAction.ClearEditing) },
                    )
                    .padding(6.dp),
                imageVector = Icons.Filled.LayersClear,
                contentDescription = "Clear",
                tint = Colors.White
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            OperationButton(
                isActive = state.currentOperation is DrawingOperation.LineDrawing,
                icon = Icons.Filled.Draw,
                click = { actioner(PhotoEditorAction.SelectDrawingMode) }
            )
            Spacer(modifier = Modifier.size(26.dp))
            OperationButton(
                isActive = state.currentOperation is DrawingOperation.TextDrawing,
                icon = Icons.Filled.TextFields,
                click = { actioner(PhotoEditorAction.SelectTextMode) }
            )
        }
    }
}

@Composable
private fun OperationButton(
    isActive: Boolean,
    icon: ImageVector,
    click: () -> Unit
) {
    Column {
        val interactionSource = remember { MutableInteractionSource() }
        Icon(
            modifier = Modifier
                .size(36.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false, color = Colors.White),
                    onClick = click,
                    enabled = !isActive
                )
                .padding(6.dp),
            imageVector = icon,
            contentDescription = "",
            tint = Colors.White
        )

        Spacer(modifier = Modifier.size(4.dp))
        if (isActive) {
            Box(
                Modifier
                    .size(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Colors.Yellow400, CircleShape)
            )
        }
    }
}

@Composable
private fun StyleSelector(
    modifier: Modifier,
    state: PhotoEditorState,
    actioner: (PhotoEditorAction) -> Unit
) {
    Row(modifier = modifier) {
        state.drawingColors.colors.forEach { color ->
            val selectedColor = color == state.drawingColors.selectedColor
            val borderSize by animateDpAsState(
                targetValue = if (selectedColor) 1.5.dp else 0.dp,
                animationSpec = tween(150)
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                val interactionSource = remember { MutableInteractionSource() }

                // Weird, but border still draw if size is 0.dp
                val modifier = if (borderSize == 0.dp) {
                    Modifier
                } else {
                    Modifier.border(width = borderSize, color = Colors.White, shape = CircleShape)
                }

                Box(
                    modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(bounded = false, color = Colors.White),
                            onClick = { actioner(PhotoEditorAction.SelectColor(color)) },
                            enabled = !selectedColor
                        )
                        .padding(3.dp)
                        .background(color = Color(color), shape = CircleShape)
                )
            }
        }

        if (state.currentOperation is DrawingOperation.TextDrawing) {
            Box(
                Modifier
                    .size(2.dp)
                    .align(Alignment.CenterVertically)
                    .background(Colors.White, CircleShape)
            )

            state.fontSize.sizes.forEach { fontSize ->
                val selectedFont = fontSize == state.fontSize.selectedSize
                val borderSize by animateDpAsState(
                    targetValue = if (selectedFont) 1.5.dp else 0.dp,
                    animationSpec = tween(150)
                )

                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    // Weird, but border still draw if size is 0.dp
                    val modifier = if (borderSize == 0.dp) {
                        Modifier
                    } else {
                        Modifier.border(
                            width = borderSize,
                            color = Colors.White,
                            shape = CircleShape
                        )
                    }

                    Box(
                        modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false, color = Colors.White),
                                onClick = { actioner(PhotoEditorAction.SelectFontSize(fontSize)) },
                                enabled = !selectedFont
                            )
                            .padding(3.dp)
                            .background(color = Colors.White, shape = CircleShape)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "A",
                            color = Colors.Black,
                            fontSize = fontSize.sp
                        )
                    }
                }
            }
        }
    }
}