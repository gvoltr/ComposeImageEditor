package com.shopmonkey.shopmonkeyapp.common.components.appbar.child

import androidx.lifecycle.ViewModel
import com.gvoltr.compose_image_editor.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChildScreenAppBarViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {
    fun navigateBack() {
        navigator.navigateBack()
    }
}
