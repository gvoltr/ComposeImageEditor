package com.gvoltr.compose_image_editor.capture

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gvoltr.compose_image_editor.navigation.Destination
import com.gvoltr.compose_image_editor.navigation.NavigationCommand

object MediaCaptureDestination : Destination {

    override val route = "capture"
    val command = NavigationCommand(route)

    override fun addToGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route = route) {
            MediaCaptureScreen()
        }
    }
}