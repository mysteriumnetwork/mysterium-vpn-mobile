package network.mysterium.provider.ui.screens.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import network.mysterium.node.Node
import network.mysterium.provider.core.CoreViewModel

class HomeViewModel(
    private val node: Node
) : CoreViewModel<Home.Event, Home.State, Home.Effect>() {

    init {
        setEvent(Home.Event.Load)
    }

    override fun createInitialState(): Home.State {
        return Home.State(
            services = emptyList(),
            isLimitReached = false,
            balance = 0.0
        )
    }

    override fun handleEvent(event: Home.Event) {
        when (event) {
            Home.Event.Load -> {
                observeServices()
                observeBalance()
                observeLimit()
            }
        }
    }

    private fun observeServices() = viewModelScope.launch {
        node.services.collect {
            setState { copy(services = it.sortedBy { it.id }) }
        }
    }

    private fun observeBalance() = viewModelScope.launch {
        node.balance.collect {
            setState { copy(balance = it) }
        }
    }

    private fun observeLimit() = viewModelScope.launch {
        node.limitMonitor.collect {
            setState { copy(isLimitReached = it) }
        }
    }
}
