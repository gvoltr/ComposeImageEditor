package com.gvoltr.compose_image_editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.gvoltr.compose_image_editor.capture.MediaCaptureDestination
import com.gvoltr.compose_image_editor.draw.PhotoEditorDestination
import com.gvoltr.compose_image_editor.navigation.NavigationManager
import com.gvoltr.compose_image_editor.preview.MediaPreviewDestination
import com.gvoltr.compose_image_editor.ui.theme.Colors
import com.gvoltr.compose_image_editor.ui.theme.ComposeImageEditorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationProvider: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Content(navigationProvider)
        }
    }
}

@Composable
fun Content(
    navigationProvider: NavigationManager
) {
    val navController = rememberNavController()
    LaunchedEffect(key1 = navController) {
        navigationProvider.setNavController(navController)
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Colors.Gray900
        )
    }

    ComposeImageEditorTheme {
        Scaffold {
            NavHost(
                navController,
                startDestination = MediaCaptureDestination.route,
                modifier = Modifier.padding(it),
            ) {
                MediaCaptureDestination.addToGraph(this)
                MediaPreviewDestination.addToGraph(this)
                PhotoEditorDestination.addToGraph(this)
            }
        }
    }
}