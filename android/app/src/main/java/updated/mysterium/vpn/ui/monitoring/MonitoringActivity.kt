package updated.mysterium.vpn.ui.monitoring

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivityMonitoringBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class MonitoringActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "MonitoringActivity"
    }

    private lateinit var binding: ActivityMonitoringBinding
    private val viewModel: MonitoringViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
    private val sessionsAdapter = SessionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
    }

    private fun configure() {
        binding.lastSessionsRecycler.apply {
            layoutManager = LinearLayoutManager(this@MonitoringActivity)
            adapter = sessionsAdapter
        }
        getLastSessions()
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onBalanceClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            navigateToMenu()
        }
    }

    private fun getLastSessions() {
        viewModel.getLastSessions().observe(this, { result ->
            result.onSuccess {
                sessionsAdapter.replaceAll(it)
            }
            result.onFailure {
                Log.i(TAG, it.localizedMessage ?: it.toString())
                // TODO("Implement error handling")
            }
        })
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
