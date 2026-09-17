package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import android.content.Intent
import android.util.Log
import network.mysterium.vpn.databinding.PopUpBillingUnavailableBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.observeOnce
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.ui.top.up.amount.usd.AmountUsdActivity
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingSummaryActivity
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity

class PlayBillingAmountUsdActivity : AmountUsdActivity() {

    private val viewModel: PlayBillingAmountUsdViewModel by inject()
    private var errorObserverRegistered = false

    override fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        // A retry must actually ask Play again: the LiveData below is backed by
        // an app-scoped singleton and would otherwise just replay its last value.
        viewModel.refreshProductDetails()
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
        if (errorObserverRegistered) {
            return
        }
        errorObserverRegistered = true
        viewModel.getSkuError().observe(this) { result ->
            result.onSuccess { errorCode ->
                errorCode.observeOnce(this) { responseCode ->
                    // Every non-OK setup response means billing is unusable right
                    // now. Previously only BILLING_UNAVAILABLE said so; the rest
                    // (SERVICE_UNAVAILABLE, SERVICE_DISCONNECTED, DEVELOPER_ERROR,
                    // ...) fell through to a "check your Wi-Fi" dialog, which is
                    // wrong and sends users and support down the wrong path.
                    Log.e(TAG, "Play Billing setup failed, response code: $responseCode")
                    billingUnavailable()
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
