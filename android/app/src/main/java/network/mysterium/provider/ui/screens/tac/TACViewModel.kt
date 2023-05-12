package network.mysterium.provider.ui.screens.tac

import network.mysterium.node.Node
import network.mysterium.node.model.NodeTerms
import network.mysterium.provider.core.CoreViewModel

class TACViewModel(
    private val node: Node
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