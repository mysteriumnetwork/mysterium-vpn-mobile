package updated.mysterium.vpn.ui.top.up.card.currency

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.CurrencyCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CardCurrencyViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()

    fun getCurrencies() = liveDataResult {
        paymentUseCase.getGateways()
            .find {
                it.name == Gateway.CARDINITY.gateway
            }
            ?.currencies
            ?.map {
                CurrencyCardItem(it)
            }
    }
}
