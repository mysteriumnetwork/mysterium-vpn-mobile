package updated.mysterium.vpn.ui.top.up.amount.usd

import android.content.Intent
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.ui.custom.view.CryptoAnimationView
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity

class TopUpAmountUsdActivity : AmountUsdActivity() {

    companion object {
        const val PAYMENT_OPTION_EXTRA_KEY = "PAYMENT_OPTION_EXTRA_KEY"
    }

    private val viewModel: TopUpAmountUsdViewModel by inject()

    override fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        PaymentOption.from(intent.extras?.getString(PAYMENT_OPTION_EXTRA_KEY))
            ?.let { paymentOption ->
                paymentOption.gateway?.let { gateway ->
                    viewModel.getAmountsUSD(gateway).observe(this) { result ->
                        result.onSuccess { list ->
                            onSuccess.invoke(list)
                        }
                        result.onFailure { exception ->
                            onFailure.invoke(exception)
                        }
                    }
                }
            }
    }

    override fun navigate() {
        PaymentOption.from(intent.extras?.getString(PAYMENT_OPTION_EXTRA_KEY))
            ?.let { paymentOption ->
                if (paymentOption == PaymentOption.MYST_ETHEREUM) {
                    navigateToCryptoPayment()
                } else if (paymentOption.gateway == Gateway.COINGATE) {
                    navigateToCryptoCurrency()
                } else {
                    navigateToCardPaymentFlow(paymentOption.gateway)
                }
            }
    }

    private fun navigateToCryptoPayment() {
        val intent = Intent(this, CryptoPaymentActivity::class.java).apply {
            val amountUSD = adapter.getSelectedValue()?.amountUsd
            putExtra(CryptoPaymentActivity.CRYPTO_AMOUNT_USD_EXTRA_KEY, amountUSD)
            putExtra(CryptoPaymentActivity.CRYPTO_NAME_EXTRA_KEY, CryptoAnimationView.ETH)
        }
        startActivity(intent)
    }

    private fun navigateToCryptoCurrency() {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity")
        ).apply {
            val amountUSD = adapter.getSelectedValue()?.amountUsd
            putExtra("CRYPTO_AMOUNT_USD_EXTRA_KEY", amountUSD)
        }
        startActivity(intent)
    }

    private fun navigateToCardPaymentFlow(gateway: Gateway?) {
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.card.currency.CardCurrencyActivity")
        ).apply {
            val amountUSD = adapter.getSelectedValue()?.amountUsd
            putExtra("AMOUNT_USD_EXTRA_KEY", amountUSD)
            putExtra("GATEWAY_EXTRA_KEY", gateway?.gateway)
        }
        startActivity(intent)
    }

}
