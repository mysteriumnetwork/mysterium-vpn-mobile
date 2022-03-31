package updated.mysterium.vpn.ui.payment.method

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.coingate.amount.TopUpAmountActivity

class PaymentMethodActivity : BaseActivity() {

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
        intent?.extras?.getStringArray(TopUpAmountActivity.PAYMENT_METHOD_EXTRA_KEY)?.let { items ->
            PaymentMethodAdapter().apply {
                replaceAll(items.toList())
                onItemSelected = {
                    navigateToTopUp(it)
                }
                binding.paymentMethodList.adapter = this
            }
        }
    }

    private fun navigateToTopUp(gateway: String) {
        val intent = Intent(this, TopUpAmountActivity::class.java).apply {
            putExtra(TopUpAmountActivity.PAYMENT_METHOD_EXTRA_KEY, gateway)
        }
        startActivity(intent)
    }
}
