package updated.mysterium.vpn.ui.top.up.card.payment

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CardPaymentViewModel(useCaseProvider: UseCaseProvider): ViewModel() {

    private val loginUseCase = useCaseProvider.login()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

}
