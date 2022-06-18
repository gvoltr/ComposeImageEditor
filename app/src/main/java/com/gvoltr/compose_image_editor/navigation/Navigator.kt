package com.gvoltr.compose_image_editor.navigation

import kotlinx.coroutines.flow.Flow

interface Navigator {
    fun navigate(directions: NavigationCommand)
    fun navigateBack()
}