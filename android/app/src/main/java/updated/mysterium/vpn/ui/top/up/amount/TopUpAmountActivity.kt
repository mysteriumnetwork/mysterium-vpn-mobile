package updated.mysterium.vpn.ui.top.up.amount

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.databinding.ActivityTopUpAmountBinding

class TopUpAmountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopUpAmountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindsAction()
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
