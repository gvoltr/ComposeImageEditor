package com.gvoltr.compose_image_editor.base

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

open class FeatureStateHolder<STATE : FeatureState>(private val initialState: STATE) {

    private val innerState = MutableStateFlow(initialState)
    val currentState = innerState as StateFlow<STATE>

    val currentValue
        get() = innerState.value

    fun setState(reducer: STATE.() -> STATE) {
        innerState.update(reducer)
    }

    fun clearState() {
        innerState.value = initialState
    }
}

@Immutable
interface FeatureState
