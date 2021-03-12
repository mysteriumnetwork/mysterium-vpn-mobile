package updated.mysterium.vpn.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityWalletBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.menu.MenuActivity

class WalletActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "WalletActivity"
    }

    private lateinit var binding: ActivityWalletBinding
    private val balanceViewModel: BalanceViewModel by inject()
    private val viewModel: WalletViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    private fun configure() {
        viewModel.getUsdEquivalent().observe(this, { result ->
            result.onSuccess {
                binding.usdEquivalentTextView.text = getString(R.string.wallet_usd_equivalent, it)
            }
            result.onFailure {
                Log.e(TAG, "Getting exchange rate failed")
                // TODO("Implement error handling")
            }
        })
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
