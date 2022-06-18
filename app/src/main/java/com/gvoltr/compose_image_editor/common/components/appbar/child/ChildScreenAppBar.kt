package com.shopmonkey.shopmonkeyapp.common.components.appbar.child

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gvoltr.compose_image_editor.ui.theme.Colors

@Composable
fun ChildScreenAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationType: NavigationType? = null,
    backgroundColor: Color = Colors.White,
    contentColor: Color = Colors.Gray900,
    viewModel: ChildScreenAppBarViewModel = hiltViewModel(),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (navigationType != null) {
                IconButton(onClick = {
                    if (onBackClick == null) {
                        viewModel.navigateBack()
                    } else {
                        onBackClick.invoke()
                    }
                }) {

                    Icons.Filled.ArrowBack

                    Icon(
                        imageVector = if (navigationType == NavigationType.GoBack) {
                            Icons.Filled.ArrowBack
                        } else {
                            Icons.Filled.Close
                        },
                        contentDescription = "Back"
                    )
                }
            }
        },
        elevation = elevation,
        actions = actions
    )
}

enum class NavigationType {
    Close, GoBack
}
