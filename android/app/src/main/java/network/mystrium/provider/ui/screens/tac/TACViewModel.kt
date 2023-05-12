package network.mystrium.provider.ui.screens.tac

import network.mystrium.node.MobileNode
import network.mystrium.node.model.NodeTerms
import network.mystrium.provider.core.CoreViewModel

class TACViewModel(
    private val node: MobileNode
) : CoreViewModel<TAC.Event, TAC.State, TAC.Effect>() {

    init {
        setEvent(TAC.Event.LoadTAC)
    }

    override fun createInitialState(): TAC.State {
        return TAC.State(terms = NodeTerms("", ""))
    }

    override fun handleEvent(event: TAC.Event) {
        when (event) {
            TAC.Event.LoadTAC -> {
                setState { copy(terms = node.terms) }
            }
        }
    }
}