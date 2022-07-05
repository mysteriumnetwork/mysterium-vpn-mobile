package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.top.up.TopUpPlayBillingCardItem
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingDataSource

class PlayBillingAmountUsdViewModel(
    useCaseProvider: UseCaseProvider,
    val playBillingDataSource: PlayBillingDataSource
) : ViewModel() {

    private val balanceUseCase = useCaseProvider.balance()

    fun getWalletEquivalent(balance: Double) = liveDataResult {
        balanceUseCase.getWalletEquivalent(balance)
    }

    fun getSkuDetails() = liveDataResult {
        playBillingDataSource.skuDetailsList.map { list ->
            list?.let {
                toTopUpPlayBillingCardItem(
                    it
                )
            }
        }
    }

    private fun toTopUpPlayBillingCardItem(
        list: List<SkuDetails>
    ): List<TopUpPlayBillingCardItem> {
        return list.map { skuDetails ->
            // Parse description from (5.99 USD) format to 5.99
            val amountUsd = skuDetails.description
                .replace("(", "")
                .replace(")", "")
                .split(" ")
                .first()
                .toDouble()

            TopUpPlayBillingCardItem(
                id = "",
                sku = skuDetails.sku,
                amountUsd = amountUsd,
                isSelected = list.indexOf(skuDetails) == 0
            )
        }
    }

}
