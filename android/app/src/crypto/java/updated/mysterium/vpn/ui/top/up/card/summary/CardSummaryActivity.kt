package updated.mysterium.vpn.ui.top.up.card.summary

import android.content.Intent
import android.util.Log
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.BaseNetworkException
import updated.mysterium.vpn.model.payment.CardOrderRequestInfo
import updated.mysterium.vpn.model.payment.OrderRequestInfo
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.ui.top.up.amount.usd.AmountUsdActivity.Companion.AMOUNT_USD_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.card.payment.CardPaymentActivity
import updated.mysterium.vpn.ui.top.up.card.payment.CardPaymentActivity.Companion.PAYMENT_URL_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.COUNTRY_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.CURRENCY_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.STATE_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.summary.SummaryActivity

class CardSummaryActivity : SummaryActivity() {

    private val viewModel: CardSummaryViewModel by inject()
    private var paymentUrl: String? = null

    override fun subscribeViewModel() {
        viewModel.paymentSuccessfully.observe(this) { paymentStatus ->
            if (paymentStatus == PaymentStatus.STATUS_PAID) {
                paymentConfirmed()
            }
        }
    }

    override fun getOrderRequestInfo(): CardOrderRequestInfo? {
        val amountUSD = intent.extras?.getDouble(AMOUNT_USD_EXTRA_KEY) ?: return null
        val country = intent.extras?.getString(COUNTRY_EXTRA_KEY) ?: return null
        val state = intent.extras?.getString(STATE_EXTRA_KEY) ?: ""
        val currency = intent.extras?.getString(CURRENCY_EXTRA_KEY) ?: return null
        val gateway = intent.extras?.getString(GATEWAY_EXTRA_KEY) ?: return null
        return CardOrderRequestInfo(amountUSD, country, state, currency, gateway)
    }

    override fun getOrder(info: OrderRequestInfo?) {
        (info as? CardOrderRequestInfo?)?.let {
            viewModel.getPayment(it).observe(this) { result ->
                result.onSuccess { order ->
                    onSuccess.invoke(order)
                    paymentUrl = order.publicGatewayData.checkoutUrl
                }
                result.onFailure { error ->
                    if (error is BaseNetworkException) {
                        Log.e(TAG, error.getMessage(this))
                        onFailure.invoke(error)
                    } else {
                        Log.e(TAG, error.localizedMessage ?: error.toString())
                        wifiNetworkErrorPopUp {
                            getOrder(info)
                        }
                    }
                }
            }
        }
    }

    override fun launchPayment() {
        paymentUrl?.let {
            val intent = Intent(this, CardPaymentActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra(PAYMENT_URL_KEY, paymentUrl)
            }
            startActivity(intent)
        }
    }

    private fun paymentConfirmed() {
        setButtonAvailability(true)
        viewModel.clearPopUpTopUpHistory()
        registerAccount()
    }

}
