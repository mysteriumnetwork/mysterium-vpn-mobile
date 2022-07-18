package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import android.content.Intent
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.observeOnce
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.ui.top.up.amount.usd.AmountUsdActivity
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingSummaryActivity

class PlayBillingAmountUsdActivity : AmountUsdActivity() {

    private val viewModel: PlayBillingAmountUsdViewModel by inject()

    override fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModel.getSkuDetails().observe(this) { result ->
            result.onSuccess { skuDetailList ->
                skuDetailList.observeOnce(this) { list ->
                    onSuccess.invoke(list)
                }
            }
            result.onFailure { exception ->
                onFailure.invoke(exception)
            }
        }
    }

    override fun navigate() {
        val intent = Intent(this, PlayBillingSummaryActivity::class.java).apply {
            putExtra(PlayBillingSummaryActivity.SKU_EXTRA_KEY, adapter.getSelectedValue())
        }
        startActivity(intent)
    }

}
