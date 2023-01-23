package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import android.content.Intent
import com.android.billingclient.api.BillingClient
import network.mysterium.vpn.databinding.PopUpBillingUnavailableBinding
import network.mysterium.vpn.databinding.PopUpWiFiErrorBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.observeOnce
import updated.mysterium.vpn.exceptions.BaseNetworkException
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.ui.top.up.amount.usd.AmountUsdActivity
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingSummaryActivity
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity

class PlayBillingAmountUsdActivity : AmountUsdActivity() {

    private val viewModel: PlayBillingAmountUsdViewModel by inject()

    override fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit,
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
        viewModel.getSkuError().observe(this) { result ->
            result.onSuccess { errorCode ->
                errorCode.observeOnce(this) {
                    if (it == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                        billingUnavailable()
                        return@observeOnce
                    }
                    onFailure.invoke(Exception())
                }
            }
            result.onFailure { exception ->
                onFailure.invoke(exception)
            }
        }
    }

    override fun navigate() {
        val intent = Intent(this, SelectCountryActivity::class.java).apply {
            putExtra(PlayBillingSummaryActivity.SKU_EXTRA_KEY, adapter.getSelectedValue())
            putExtra(PAYMENT_OPTION_EXTRA_KEY, PaymentOption.GOOGLE.value)
        }
        startActivity(intent)
    }

    private fun billingUnavailable() {
        val bindingPopUp = PopUpBillingUnavailableBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}
