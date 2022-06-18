package com.gvoltr.compose_image_editor.navigation

import androidx.navigation.NavOptions

data class NavigationCommand(
    val destination: String,
    val navOptions: NavOptions? = null
)