package updated.mysterium.vpn.ui.top.up.amount.usd

import android.content.Intent
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.ui.custom.view.CryptoAnimationView
import updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity.Companion.CRYPTO_NAME_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.COUNTRY_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.CURRENCY_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.STATE_EXTRA_KEY

class TopUpAmountUsdActivity : AmountUsdActivity() {

    companion object {
        const val CRYPTO_AMOUNT_USD_EXTRA_KEY = "CRYPTO_AMOUNT_USD_EXTRA_KEY"
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
        val amountUSD = adapter.getSelectedValue()?.amountUsd
        val country = intent?.extras?.getString(COUNTRY_EXTRA_KEY)
        val stateOfAmerica = intent?.extras?.getString(STATE_EXTRA_KEY)
        val intent = Intent(this, CryptoPaymentActivity::class.java).apply {
            putExtra(CRYPTO_AMOUNT_USD_EXTRA_KEY, amountUSD)
            putExtra(COUNTRY_EXTRA_KEY, country)
            putExtra(STATE_EXTRA_KEY, stateOfAmerica)
            putExtra(CRYPTO_NAME_EXTRA_KEY, CryptoAnimationView.ETH)
        }
        startActivity(intent)
    }

    private fun navigateToCryptoCurrency() {
        val amountUSD = adapter.getSelectedValue()?.amountUsd
        val country = intent?.extras?.getString(COUNTRY_EXTRA_KEY)
        val stateOfAmerica = intent?.extras?.getString(STATE_EXTRA_KEY)
        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity")
        ).apply {
            putExtra(CRYPTO_AMOUNT_USD_EXTRA_KEY, amountUSD)
            putExtra(COUNTRY_EXTRA_KEY, country)
            putExtra(STATE_EXTRA_KEY, stateOfAmerica)
        }
        startActivity(intent)
    }

    private fun navigateToCardPaymentFlow(gateway: Gateway?) {
        val amountUSD = adapter.getSelectedValue()?.amountUsd
        val currency = intent?.extras?.getString(CURRENCY_EXTRA_KEY)
        val country = intent?.extras?.getString(COUNTRY_EXTRA_KEY)
        val stateOfAmerica = intent?.extras?.getString(STATE_EXTRA_KEY)

        val intent = Intent(
            this,
            Class.forName("updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryActivity")
        ).apply {
            putExtra(AMOUNT_USD_EXTRA_KEY, amountUSD)
            putExtra(GATEWAY_EXTRA_KEY, gateway?.gateway)
            putExtra(CURRENCY_EXTRA_KEY, currency)
            putExtra(COUNTRY_EXTRA_KEY, country)
            putExtra(STATE_EXTRA_KEY, stateOfAmerica)
        }
        startActivity(intent)
    }

}
