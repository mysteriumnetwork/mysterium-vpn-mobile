package updated.mysterium.vpn.ui.payment.method

import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPaymentMethodBinding
import updated.mysterium.vpn.ui.base.BaseActivity

class PaymentMethodActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
    }

    private lateinit var binding: ActivityPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
