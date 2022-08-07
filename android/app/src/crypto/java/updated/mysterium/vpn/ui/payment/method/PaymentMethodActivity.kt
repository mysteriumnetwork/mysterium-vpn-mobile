package updated.mysterium.vpn.ui.payment.method

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.amount.usd.TopUpAmountUsdActivity

class PaymentMethodActivity : BaseActivity() {

    companion object {
        private const val GATEWAYS_EXTRA = "gatewaysExtra"
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
            mutableListOf<PaymentOption>().apply {
                intent.getStringArrayExtra(GATEWAYS_EXTRA)
                    ?.mapNotNull { Gateway.from(it) }
                    ?.mapNotNull { PaymentOption.from(it) }
                    ?.let { this.addAll(it) }
                    ?.let { this.add(PaymentOption.MYST_TOTAL) }
            }
        }
    }

    private fun onPaymentOptionSelected(paymentOption: PaymentOption) {
        if (paymentOption == PaymentOption.MYST_TOTAL) {
            navigateToMystChainSelect()
        } else paymentOption.gateway?.let {
            navigateToTopUp(it)
        }
    }

    private fun navigateToMystChainSelect() {
        val intent = Intent(this, PaymentMethodActivity::class.java).apply {
            putExtra(MYST_CHAIN_EXTRA, true)
        }
        startActivity(intent)
    }

    private fun navigateToTopUp(gateway: Gateway) {
        val intent = Intent(this, TopUpAmountUsdActivity::class.java).apply {
            putExtra(TopUpAmountUsdActivity.PAYMENT_METHOD_EXTRA_KEY, gateway.gateway)
        }
        startActivity(intent)
    }
}
