package updated.mysterium.vpn.ui.top.up.amount.usd

import android.content.Intent
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem

class TopUpAmountUsdActivity : AmountUsdActivity() {

    companion object {
        const val PAYMENT_METHOD_EXTRA_KEY = "PAYMENT_METHOD_EXTRA_KEY"
    }

    private val viewModel: TopUpAmountUsdViewModel by inject()

    override fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        Gateway.from(intent.extras?.getString(PAYMENT_METHOD_EXTRA_KEY))?.let { gateway ->
            viewModel.getAmountsUSD(gateway).observe(this) {
                it.onSuccess { list ->
                    onSuccess.invoke(list)
                }
                it.onFailure { exception ->
                    onFailure.invoke(exception)
                }
            }
        }
    }

    override fun navigate() {
        Gateway.from(intent.extras?.getString(PAYMENT_METHOD_EXTRA_KEY))?.let { gateway ->
            if (gateway == Gateway.COINGATE) {
                navigateToCryptoPaymentFlow()
            } else {
                navigateToCardPaymentFlow(gateway)
            }
        }
    }

    private fun navigateToCryptoPaymentFlow() {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity")
        ).apply {
            val amountUSD = adapter.getSelectedValue()?.amountUsd
            putExtra("CRYPTO_AMOUNT_USD_EXTRA_KEY", amountUSD)
        }
        startActivity(intent)
    }

    private fun navigateToCardPaymentFlow(gateway: Gateway) {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.card.currency.CardCurrencyActivity")
        ).apply {
            val amountUSD = adapter.getSelectedValue()?.amountUsd
            putExtra("AMOUNT_USD_EXTRA_KEY", amountUSD)
            putExtra("GATEWAY_EXTRA_KEY", gateway.gateway)
        }
        startActivity(intent)
    }

}
