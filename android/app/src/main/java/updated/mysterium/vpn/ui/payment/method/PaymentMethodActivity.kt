package updated.mysterium.vpn.ui.payment.method

import android.content.Context
import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.price.TopUpPriceActivity

class PaymentMethodActivity : BaseActivity() {

    companion object {

        private const val GATEWAYS_EXTRA = "gatewaysExtra"

        fun newIntent(context: Context, gateways: List<String>): Intent {
            val intent = Intent(context, PaymentMethodActivity::class.java)
            intent.putExtra(GATEWAYS_EXTRA, gateways.toTypedArray())
            return intent
        }
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
        val gateways = intent
            .getStringArrayExtra(GATEWAYS_EXTRA)
            ?.map { gateway -> Gateway.from(gateway) }

        gateways?.let {
            PaymentMethodAdapter().apply {
                replaceAll(it.requireNoNulls())
                onItemSelected = {
                    navigateToTopUp(it)
                }
                binding.paymentMethodList.adapter = this
            }
        }
    }

    private fun navigateToTopUp(gateway: Gateway) {
        if (gateway == Gateway.GOOGLE) {
            startActivity(Intent(this, TopUpPriceActivity::class.java))
        }
    }
}
