package network.mysterium.provider.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface UIEffect
interface UIEvent
interface UIState

abstract class CoreViewModel<Event : UIEvent, State : UIState, Effect : UIEffect> : ViewModel() {

    val currentState: State
        get() = uiState.value
    val uiState: StateFlow<State>
    val event: SharedFlow<Event>
    val effect: Flow<Effect>

    private val initialState: State by lazy { createInitialState() }
    private val uiStateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    private val evenSharedFlow: MutableSharedFlow<Event> = MutableSharedFlow()
    private val effectChannel: Channel<Effect> = Channel()

    init {
        uiState = uiStateFlow.asStateFlow()
        event = evenSharedFlow.asSharedFlow()
        effect = effectChannel.receiveAsFlow()
        subscribeEvents()
    }

    abstract fun createInitialState(): State
    abstract fun handleEvent(event: Event)

    fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { evenSharedFlow.emit(newEvent) }
    }

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        uiStateFlow.value = newState
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { effectChannel.send(effectValue) }
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }
}
