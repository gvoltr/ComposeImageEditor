package com.gvoltr.compose_image_editor.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gvoltr.compose_image_editor.ui.theme.Colors
import com.shopmonkey.shopmonkeyapp.common.components.appbar.child.ChildScreenAppBar
import com.shopmonkey.shopmonkeyapp.common.components.appbar.child.NavigationType

@Composable
fun PhotoEditorScreen(
    viewModel: PhotoEditorViewModel = hiltViewModel()
) {
    val state by viewModel.currentState.collectAsState()
    PhotoEditorContent(state = state, actioner = viewModel::submitAction)
}

@Composable
private fun PhotoEditorContent(
    state: PhotoEditorState,
    actioner: (PhotoEditorAction) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Colors.Gray900)
    ) {
        AppBar(actioner = actioner)
        PhotoCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = state,
            actioner = actioner
        )
        Controls(
            state = state,
            actioner = actioner
        )
    }
}

@Composable
private fun AppBar(
    actioner: (PhotoEditorAction) -> Unit
) {
    ChildScreenAppBar(
        title = "Edit Photo",
        backgroundColor = Colors.Gray900,
        contentColor = Colors.White,
        navigationType = NavigationType.GoBack,
        actions = {
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            actioner(PhotoEditorAction.Save)
                        }
                    )
                    .padding(14.dp),
                text = "Save",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.W500,
                    color = Colors.White
                ),
                color = Colors.White
            )
        }
    )
}