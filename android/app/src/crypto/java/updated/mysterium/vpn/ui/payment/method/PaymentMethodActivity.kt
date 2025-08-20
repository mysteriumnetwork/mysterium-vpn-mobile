package updated.mysterium.vpn.ui.payment.method

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity

class PaymentMethodActivity : BaseActivity() {

    companion object {
        const val MYST_CHAIN_EXTRA_KEY = "MYST_CHAIN_EXTRA_KEY"
    }

    private lateinit var binding: ActivityPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        setUpPaymentMethodList()
        applyInsets(binding.root)
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setUpPaymentMethodList() {
        PaymentMethodAdapter().apply {
            replaceAll(getPaymentOptions())
            onItemSelected = ::onPaymentOptionSelected
            binding.paymentMethodList.adapter = this
        }
    }

    private fun getPaymentOptions(): List<PaymentOption> {
        return if (intent.extras?.getBoolean(MYST_CHAIN_EXTRA_KEY) == true) {
            listOf(PaymentOption.MYST_ETHEREUM, PaymentOption.MYST_POLYGON)
        } else {
            val extras = intent.getStringArrayExtra(PAYMENT_OPTION_EXTRA_KEY)
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
            else -> navigateToCountrySelect(paymentOption)
        }
    }

    private fun navigateToMystChainSelect() {
        val intent = Intent(this, PaymentMethodActivity::class.java).apply {
            putExtra(MYST_CHAIN_EXTRA_KEY, true)
        }
        startActivity(intent)
    }

    private fun navigateToCryptoPayment() {
        val intent = Intent(this, CryptoPaymentActivity::class.java).apply {
            putExtra(MYST_POLYGON_EXTRA_KEY, true)
        }
        startActivity(intent)
    }

    private fun navigateToCountrySelect(paymentOption: PaymentOption) {
        val intent = Intent(this, SelectCountryActivity::class.java).apply {
            putExtra(PAYMENT_OPTION_EXTRA_KEY, paymentOption.value)
        }
        startActivity(intent)
    }

}
