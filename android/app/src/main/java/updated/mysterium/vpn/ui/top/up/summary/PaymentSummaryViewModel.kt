package updated.mysterium.vpn.ui.top.up.summary

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PaymentSummaryViewModel(useCaseProvider: UseCaseProvider) :
    ViewModel() {

    private val loginUseCase = useCaseProvider.login()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }
}
