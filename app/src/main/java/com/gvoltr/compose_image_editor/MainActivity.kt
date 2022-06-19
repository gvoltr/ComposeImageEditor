package com.gvoltr.compose_image_editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gvoltr.compose_image_editor.capture.MediaCaptureNavigation
import com.gvoltr.compose_image_editor.draw.ImageEditorNavigation
import com.gvoltr.compose_image_editor.navigation.NavigationManager
import com.gvoltr.compose_image_editor.preview.MediaPreviewNavigation
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

    ComposeImageEditorTheme {
        // A surface container using the 'background' color from the theme
        Scaffold {
            NavHost(
                navController,
                startDestination = MediaCaptureNavigation.route,
                modifier = Modifier.padding(it),
            ) {
                MediaCaptureNavigation.addToGraph(this)
                MediaPreviewNavigation.addToGraph(this)
                ImageEditorNavigation.addToGraph(this)
            }
        }
    }
}