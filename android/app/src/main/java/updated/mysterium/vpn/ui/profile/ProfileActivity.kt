package updated.mysterium.vpn.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityProfileBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity


class ProfileActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "ProfileActivity"
        const val COPY_LABEL = "User identity address"
    }

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
    private var identityAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    private fun configure() {
        viewModel.getIdentity().observe(this, { result ->
            result.onSuccess { identity ->
                binding.identityValueTextView.text = identity.address
                identityAddress = identity.address
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        })
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun bindsAction() {
        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }
        binding.manualConnectToolbar.onBalanceClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
    }

    private fun copyToClipboard() {
        getSystemService(this, ClipboardManager::class.java)?.let { clipManager ->
            val clipData = ClipData.newPlainText(COPY_LABEL, identityAddress)
            clipManager.setPrimaryClip(clipData)
            showToast()
        }
    }

    private fun showToast() {
        Toast.makeText(
            this,
            getString(R.string.profile_copy_to_clipboard),
            Toast.LENGTH_LONG
        ).show()
    }
}
