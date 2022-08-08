package updated.mysterium.vpn.ui.payment.method

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.amount.usd.TopUpAmountUsdActivity
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity.Companion.MYST_POLYGON_EXTRA_KEY

class PaymentMethodActivity : BaseActivity() {

    companion object {
        private const val PAYMENT_OPTIONS_EXTRA = "paymentOptionsExtra"
        private const val MYST_CHAIN_EXTRA = "mystChainExtra"
    }

    private lateinit var binding: ActivityPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        setUpPaymentMethodList()
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setUpPaymentMethodList() {
        PaymentMethodAdapter().apply {
            replaceAll(getPaymentOptions())
            onItemSelected = {
                onPaymentOptionSelected(it)
            }
            binding.paymentMethodList.adapter = this
        }
    }

    private fun getPaymentOptions(): List<PaymentOption> {
        return if (intent.extras?.getBoolean(MYST_CHAIN_EXTRA) == true) {
            listOf(PaymentOption.MYST_ETHEREUM, PaymentOption.MYST_POLYGON)
        } else {
            val extras = intent.getStringArrayExtra(PAYMENT_OPTIONS_EXTRA)
                ?.mapNotNull { PaymentOption.from(it) } ?: emptyList()
            mutableListOf<PaymentOption>().apply {
                this.addAll(extras)
                this.add(PaymentOption.MYST_TOTAL)
            }
        }
    }

    private fun onPaymentOptionSelected(paymentOption: PaymentOption) {
        when (paymentOption) {
            PaymentOption.MYST_TOTAL -> navigateToMystChainSelect()
            PaymentOption.MYST_POLYGON -> navigateToCryptoPayment()
            else -> navigateToTopUp(paymentOption)
        }
    }

    private fun navigateToMystChainSelect() {
        val intent = Intent(this, PaymentMethodActivity::class.java).apply {
            putExtra(MYST_CHAIN_EXTRA, true)
        }
        startActivity(intent)
    }

    private fun navigateToCryptoPayment() {
        val intent = Intent(this, CryptoPaymentActivity::class.java).apply {
            putExtra(MYST_POLYGON_EXTRA_KEY, true)
        }
        startActivity(intent)
    }

    private fun navigateToTopUp(paymentOption: PaymentOption) {
        val intent = Intent(this, TopUpAmountUsdActivity::class.java).apply {
            putExtra(TopUpAmountUsdActivity.PAYMENT_OPTION_EXTRA_KEY, paymentOption.value)
        }
        startActivity(intent)
    }
}
