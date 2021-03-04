package updated.mysterium.vpn.ui.wallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityWalletBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.menu.MenuActivity

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private val balanceViewModel: BalanceViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.balanceTextView.text = getString(R.string.wallet_current_balance, it)
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
    }
}
