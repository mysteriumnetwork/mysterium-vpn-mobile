package updated.mysterium.vpn.ui.top.up.card.currency

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.CurrencyCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SelectCountryViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()

    fun getCurrencies(gateway: String) = liveDataResult {
        paymentUseCase.getGateways()
            .find {
                it.name == gateway
            }
            ?.currencies
            ?.map {
                CurrencyCardItem(it)
            }
    }
}
