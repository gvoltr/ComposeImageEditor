package com.gvoltr.compose_image_editor.navigation

import androidx.navigation.NavGraphBuilder

interface Destination {
    val route: String
    val command: NavigationCommand
    fun addToGraph(navGraphBuilder: NavGraphBuilder)
}