package com.gvoltr.compose_image_editor.common.components.permission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gvoltr.compose_image_editor.ui.theme.Colors

@Composable
fun PermissionNotAvailableDefaultContent(
    message: String,
    buttonTitle: String,
    isButtonEnabled: Boolean,
    action: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.h3,
                color = Colors.Gray900,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(20.dp))

            Button(
                modifier = Modifier.height(44.dp),
                shape = CircleShape,
                enabled = isButtonEnabled,
                onClick = {
                    action()
                }
            ) {
                Text(
                    text = buttonTitle,
                    style = MaterialTheme.typography.body1.copy(color = Colors.White)
                )
            }
        }
    }
}
