package com.gvoltr.compose_image_editor.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NavigationModule {

    @Singleton
    @Provides
    fun providesNavigationManager() = NavigationManager()

    @Provides
    @Singleton
    fun providesNavigator(manager: NavigationManager) = manager as Navigator
}