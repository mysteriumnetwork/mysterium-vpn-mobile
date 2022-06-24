package updated.mysterium.vpn.ui.top.up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.android.billingclient.api.SkuDetails
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.summary.BillingDataSource

class TopUpPaymentViewModel(
    useCaseProvider: UseCaseProvider,
    val billingDataSource: BillingDataSource
) : ViewModel() {

    private val paymentUseCase = useCaseProvider.payment()
    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()

    companion object {
        val filterRange = ('0'..'9') + ('.')
    }

    fun clearPopUpTopUpHistory() {
        balanceUseCase.clearBalancePopUpHistory()
        balanceUseCase.clearMinBalancePopUpHistory()
        balanceUseCase.clearBalancePushHistory()
        balanceUseCase.clearMinBalancePushHistory()
    }

    fun registerAccount() = liveDataResult {
        val identity = connectionUseCase.getIdentity()
        val identityModel = IdentityModel(identity)
        val req = RegisterIdentityRequest().apply {
            identityAddress = identityModel.address
            token?.let {
                this.token = it
            }
        }
        connectionUseCase.registerIdentity(req)
        connectionUseCase.registrationFees()
    }

    fun isBalanceLimitExceeded() = liveDataResult {
        paymentUseCase.isBalanceLimitExceeded()
    }

    fun getSkuDetails() = liveDataResult {
        billingDataSource.skuDetailsList.map { list ->
            list?.let {
                toTopUpPriceCardItem(
                    it
                )
            }
        }
    }

    private fun toTopUpPriceCardItem(
        list: List<SkuDetails>
    ): List<TopUpPriceCardItem> {
        return list.map { skuDetails ->
            val price = skuDetails.description
                .replace(',', '.')
                .replace('_', '.')
                .filter { it in filterRange }
                .toDouble()
            TopUpPriceCardItem(
                id = "",
                sku = skuDetails.sku,
                price = price,
                isSelected = list.indexOf(skuDetails) == 0
            )
        }
    }

}
