package updated.mysterium.vpn.ui.top.up.card

import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityCardPaymentBinding
import updated.mysterium.vpn.ui.base.BaseActivity

class CardPaymentActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val REGISTRATION_MODE_EXTRA_KEY = "REGISTRATION_MODE_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCardPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.confirmButton.isEnabled = false
    }


}
