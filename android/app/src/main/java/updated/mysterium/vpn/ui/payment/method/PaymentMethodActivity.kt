package updated.mysterium.vpn.ui.payment.method

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.coingate.amount.TopUpAmountActivity

class PaymentMethodActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
    }

    private fun bind() {
        binding.cryptoPayment.setOnClickListener {
            val intent = Intent(this, TopUpAmountActivity::class.java).apply {
                putExtra(TopUpAmountActivity.PAYMENT_METHOD_EXTRA_KEY, Gateway.COINGATE)
            }
            startActivity(intent)
        }
        binding.creditCardPayment.setOnClickListener {
            val intent = Intent(this, TopUpAmountActivity::class.java).apply {
                putExtra(TopUpAmountActivity.PAYMENT_METHOD_EXTRA_KEY, Gateway.CARDINITY)
            }
            startActivity(intent)
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
