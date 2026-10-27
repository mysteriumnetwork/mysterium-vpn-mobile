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
    private var observersRegistered = false

    override fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        // A retry must actually ask Play again: the LiveData below is backed by
        // an app-scoped singleton and would otherwise just replay its last value.
        viewModel.refreshProductDetails()
        if (observersRegistered) {
            return
        }
        observersRegistered = true
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
            result.onSuccess { failures ->
                // observe, not observeOnce: SingleLiveEvent already delivers a
                // value once, and observeOnce cannot remove itself from one --
                // SingleLiveEvent.observe registers a wrapper, so the
                // removeObserver inside observeOnce matches nothing.
                failures.observe(this) { failure ->
                    // Null is a retraction: a later query succeeded, so a
                    // failure that was still queued here no longer holds.
                    if (failure == null) {
                        return@observe
                    }
                    Log.e(TAG, "Play Billing failed, response code: ${failure.responseCode}")
                    // A blip that will clear on its own gets a retry. Only a
                    // failure nothing here can fix gets the dialog whose single
                    // button drops the user out of the top-up flow.
                    if (failure.retryable) {
                        showNoAmountPopUp { loadAmounts() }
                    } else {
                        billingUnavailable()
                    }
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
