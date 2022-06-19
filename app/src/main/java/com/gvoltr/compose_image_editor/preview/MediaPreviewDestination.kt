package com.gvoltr.compose_image_editor.preview

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gvoltr.compose_image_editor.navigation.Destination
import com.gvoltr.compose_image_editor.navigation.NavigationCommand

object MediaPreviewDestination : Destination {
    const val argSelectedMedia = "selected_media"
    override val route = "media_preview"

    override fun addToGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
            route = "$route/{$argSelectedMedia}",
            arguments = listOf(navArgument(argSelectedMedia) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            MediaPreviewScreen()
        }
    }

    fun createCommand(selectedMedia: String) =
        NavigationCommand(destination = "$route/$selectedMedia")
}