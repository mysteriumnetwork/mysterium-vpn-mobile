package updated.mysterium.vpn.ui.top.up.card.summary

import android.os.Bundle
import android.util.Log
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.payment.CardOrder
import updated.mysterium.vpn.ui.base.BaseActivity

class CardSummaryActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val CRYPTO_CURRENCY_EXTRA_KEY = "CRYPTO_CURRENCY_EXTRA_KEY"
        const val COUNTRY_EXTRA_KEY = "COUNTRY_EXTRA_KEY"
        private const val TAG = "CardSummaryActivity"
    }

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: CardSummaryViewModel by inject()
    private var paymentHtml: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        getMystAmount()
        loadPayment()
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            launchCardinityPayment()
        }
    }

    private fun getMystAmount() {
        val mystAmount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)
        binding.mystTextView.text = getString(
            R.string.card_payment_myst_description, mystAmount
        )
    }

    private fun loadPayment() {
        val amount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY) ?: return
        val currency = intent.extras?.getString(CRYPTO_CURRENCY_EXTRA_KEY) ?: return
        val country = intent.extras?.getString(COUNTRY_EXTRA_KEY) ?: return
        viewModel.getPayment(amount, country, currency).observe(this) {
            it.onSuccess { order ->
                inflateOrderData(order)
                paymentHtml = order.html
            }
            it.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
            }
        }
    }

    private fun inflateOrderData(cardOrder: CardOrder) {
        binding.mystValueTextView.text = getString(
            R.string.card_payment_myst_amount, cardOrder.payAmount.toFloat()
        )
        binding.vatValueTextView.text = getString(
            R.string.card_payment_myst_amount, cardOrder.taxes.toFloat()
        )
        binding.totalValueTextView.text = getString(
            R.string.card_payment_myst_amount, cardOrder.orderTotalAmount.toFloat()
        )
    }

    private fun launchCardinityPayment() {
        Log.d(TAG, paymentHtml.toString())
    }
}
