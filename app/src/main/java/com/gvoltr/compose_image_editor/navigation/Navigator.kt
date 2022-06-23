package com.gvoltr.compose_image_editor.navigation


interface Navigator {
    fun navigate(directions: NavigationCommand)
    fun navigateBack()
}