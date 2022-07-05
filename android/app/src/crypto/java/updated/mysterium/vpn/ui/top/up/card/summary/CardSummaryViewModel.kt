package updated.mysterium.vpn.ui.top.up.card.summary

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CardSummaryViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }
}
