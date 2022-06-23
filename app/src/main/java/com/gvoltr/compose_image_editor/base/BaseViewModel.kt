package com.gvoltr.compose_image_editor.base

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class BaseViewModel<STATE : State, ACTION, EFFECT>(initialState: STATE) : ViewModel() {

    private val innerState = MutableStateFlow(initialState)
    val currentState = innerState as StateFlow<STATE>

    protected val currentValue
        get() = innerState.value

    private val actionSink = MutableSharedFlow<ACTION>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val effectSink = MutableSharedFlow<EFFECT>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * [ACTION]s are typically user-initiated actions performed in the UI, for example, clicking a
     * button. They are fired by the View.
     */
    private val actions = actionSink.shareIn(viewModelScope, SharingStarted.Lazily)

    /**
     * [EFFECT]s are one-off events by the ViewModel that should be handled
     * immediately; for example, transient toasts,
     * banners, or logging events. They are typically unrelated to rendering the UI state.
     */
    val effects = effectSink.shareIn(viewModelScope, SharingStarted.Lazily)

    init {
        viewModelScope.launch {
            actions.collect {
                onAction(it)
            }
        }
    }

    protected open suspend fun onAction(action: ACTION) {
    }

    fun submitAction(action: ACTION) {
        actionSink.tryEmit(action)
    }

    protected fun emitEffect(effect: EFFECT) {
        effectSink.tryEmit(effect)
    }

    fun setState(reducer: STATE.() -> STATE) {
        innerState.update(reducer)
    }
}

@Immutable
interface State