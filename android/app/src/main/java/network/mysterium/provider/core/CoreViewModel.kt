package network.mysterium.provider.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
import kotlin.coroutines.CoroutineContext

interface UIEffect
interface UIEvent
interface UIState

abstract class CoreViewModel<Event : UIEvent, State : UIState, Effect : UIEffect> : ViewModel() {

    val currentState: State
        get() = uiState.value
    val uiState: StateFlow<State>
    val event: SharedFlow<Event>
    val effect: Flow<Effect>

    protected val mainDispatcher = Dispatchers.Main
    protected val ioDispatcher = Dispatchers.IO

    protected val defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Default handler", throwable.message, throwable)
        Sentry.captureException(throwable)
    }

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

    protected fun launch(
        dispatcher: CoroutineContext = mainDispatcher,
        scope: CoroutineScope = viewModelScope,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return scope.launch(dispatcher + defaultErrorHandler) {
            this.block()
        }
    }

    fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch(defaultErrorHandler) { evenSharedFlow.emit(newEvent) }
    }

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        uiStateFlow.value = newState
    }

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch(defaultErrorHandler) { effectChannel.send(effectValue) }
    }

    private fun subscribeEvents() {
        viewModelScope.launch(defaultErrorHandler) {
            event.collect {
                handleEvent(it)
            }
        }
    }
}
