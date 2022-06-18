package com.gvoltr.compose_image_editor.navigation

import androidx.navigation.NavController

class NavigationManager  : Navigator {
    private var navController: NavController? = null

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun navigate(directions: NavigationCommand) {
        navController?.apply {
            navigate(
                directions.destination,
                directions.navOptions
            )
        }
    }

    override fun navigateBack() {
        navController?.popBackStack()
    }
}