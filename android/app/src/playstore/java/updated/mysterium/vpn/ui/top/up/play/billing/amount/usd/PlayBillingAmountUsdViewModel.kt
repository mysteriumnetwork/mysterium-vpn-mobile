package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.android.billingclient.api.ProductDetails
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
        list: List<ProductDetails>
    ): List<TopUpPlayBillingCardItem> {
        return list.mapNotNull { productDetails ->
            // Description is formatted as "(5.99 USD)"; skip anything unparseable.
            val amountUsd = parseAmountUsd(productDetails.description)
                ?: return@mapNotNull null

            TopUpPlayBillingCardItem(
                id = "",
                sku = productDetails.productId,
                amountUsd = amountUsd,
                isSelected = list.indexOf(productDetails) == 0
            )
        }
    }

}
