package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.android.billingclient.api.SkuDetails
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.top.up.TopUpPlayBillingCardItem
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingDataSource

class PlayBillingAmountUsdViewModel(
    private val playBillingDataSource: PlayBillingDataSource
) : ViewModel() {

    fun getSkuDetails() = liveDataResult {
        playBillingDataSource.skuDetailsList.map { list ->
            list?.let {
                toTopUpPlayBillingCardItem(
                    it
                )
            }
        }
    }

    fun getSkuError() = liveDataResult {
        playBillingDataSource.skuDetailsError
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
