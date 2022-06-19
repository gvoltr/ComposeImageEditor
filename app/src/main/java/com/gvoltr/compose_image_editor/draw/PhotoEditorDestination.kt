package com.gvoltr.compose_image_editor.draw

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gvoltr.compose_image_editor.navigation.Destination
import com.gvoltr.compose_image_editor.navigation.NavigationCommand

object PhotoEditorDestination : Destination {
    const val argSelectedPhoto = "selected_photo"
    override val route = "editor"

    override fun addToGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
            route = "${route}/{${argSelectedPhoto}}",
            arguments = listOf(navArgument(argSelectedPhoto) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            PhotoEditorScreen()
        }
    }

    fun createCommand(selectedMedia: String) =
        NavigationCommand(destination = "${route}/$selectedMedia")
}