package com.gvoltr.compose_image_editor.draw

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gvoltr.compose_image_editor.navigation.Destination
import com.gvoltr.compose_image_editor.navigation.NavigationCommand

object ImageEditorNavigation : Destination {

    override val route = "editor"
    override val command = NavigationCommand(route)

    override fun addToGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(route = route) {
            ImageEditorScreen()
        }
    }
}