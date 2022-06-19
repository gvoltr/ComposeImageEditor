package com.gvoltr.compose_image_editor.navigation

import androidx.navigation.NavGraphBuilder

interface Destination {
    val route: String
    fun addToGraph(navGraphBuilder: NavGraphBuilder)
}