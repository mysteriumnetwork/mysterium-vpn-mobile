package updated.mysterium.vpn.ui.manual.connect.home

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.AppNotificationManager
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.ui.DisplayMoneyOptions
import network.mysterium.ui.PriceUtils
import network.mysterium.ui.StatisticsModel
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityHomeBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.ProposalModel
import updated.mysterium.vpn.ui.manual.connect.select.node.SelectNodeActivity

class HomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PROPOSAL_MODEL = "PROPOSAL_MODEL"
        private const val TAG = "HomeActivity"
        private const val CURRENCY = "MYSTT"
    }

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by inject()
    private val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()
    private lateinit var appNotificationManager: AppNotificationManager
    private lateinit var proposalModel: ProposalModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadIpAddress()
        bindsAction()
        subscribeViewModel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkProposalArgument()
    }

    private fun subscribeViewModel() {
        viewModel.statisticsUpdate.observe(this, {
            val statistics = StatisticsModel.from(it)
            updateNodeStatistics(statistics)
            updateNotificationStatistics(statistics)
        })
        viewModel.connectionState.observe(this, {
            when (it) {
                "NotConnected" -> disconnect()
                "Connecting" -> inflateConnectingCardView()
                "Connected" -> {
                    loadIpAddress()
                    inflateConnectedCardView()
                }
            }
        })
    }

    private fun updateNodeStatistics(statistics: StatisticsModel) {
        binding.connectionState.updateConnectedStatistics(statistics, CURRENCY)
    }

    private fun updateNotificationStatistics(statistics: StatisticsModel) {
        val countryName = proposalModel.countryName
        val notificationTitle = getString(R.string.notification_title_connected, countryName)
        val tokensSpent = PriceUtils.displayMoney(
            ProposalPaymentMoney(
                amount = statistics.tokensSpent,
                currency = CURRENCY
            ),
            DisplayMoneyOptions(fractionDigits = 3, showCurrency = true)
        )
        val notificationContent = getString(
            R.string.notification_content,
            "${statistics.bytesReceived.value} ${statistics.bytesReceived.units}",
            "${statistics.bytesSent.value} ${statistics.bytesSent.units}",
            tokensSpent
        )
        viewModel.showStatisticsNotification(notificationTitle, notificationContent)
    }

    private fun checkProposalArgument() {
        intent.extras?.getParcelable<ProposalModel>(EXTRA_PROPOSAL_MODEL)?.let {
            viewModel.disconnect()
            proposalModel = it
            bindMysteriumService()
            viewModel.connectNode(it, deferredMysteriumCoreService, appNotificationManager).observe(
                this, { result ->
                result.onSuccess {
                    loadIpAddress()
                    inflateConnectedCardView()
                }
                result.onFailure { throwable ->
                    Log.i(TAG, "Connection failed: ${throwable.localizedMessage}")
                    // TODO("Implement error handling")
                }
            })
            inflateNodeInfo()
            inflateConnectingCardView()
        }
    }

    private fun bindMysteriumService() {
        appNotificationManager = AppNotificationManager(
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager,
            deferredMysteriumCoreService
        ).apply { init(this@HomeActivity) }
        Intent(this, MysteriumAndroidCoreService::class.java).also { intent ->
            bindService(
                intent,
                object : ServiceConnection {

                    override fun onServiceDisconnected(name: ComponentName?) {
                        Log.i(TAG, "Service disconnected")
                    }

                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        Log.i(TAG, "Service connected")
                        deferredMysteriumCoreService.complete(service as MysteriumCoreService)
                    }
                },
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun loadIpAddress() {
        binding.ipTextView.text = getString(R.string.manual_connect_loading)
        viewModel.getLocation().observe(this, { result ->
            result.onSuccess {
                binding.ipTextView.text = it.ip
            }
            result.onFailure {
                Log.e(TAG, "Data loading failed")
                binding.ipTextView.text = getString(R.string.manual_connect_unknown)
                // TODO("Implement error handling")
            }
        })
    }

    private fun bindsAction() {
        binding.selectAnotherNodeButton.setOnClickListener {
            navigateToSelectNode()
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            viewModel.addToFavourite(proposalModel).observe(this, { result ->
                result.onSuccess {
                    Toast.makeText(
                        this,
                        getString(R.string.manual_connect_saved_to_favourite),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                result.onFailure {
                    Log.e(TAG, "Saving failed")
                    // TODO("Implement error handling")
                }
            })
        }
        binding.connectionState.initListeners(
            selectNodeManually = {
                navigateToSelectNode()
            },
            disconnect = {
                viewModel.disconnect()
            }
        )
    }

    private fun disconnect() {
        binding.connectionState.showDisconnectedState()
        binding.connectedNodeInfo.visibility = View.INVISIBLE
        binding.titleTextView.text = getString(R.string.manual_connect_disconnected)
        binding.securityStatusTextView.visibility = View.INVISIBLE
        binding.securityStatusImageView.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.short_divider)
        )
        binding.connectionTypeTextView.text = getString(R.string.manual_connect_country_status)
        binding.connectionTypeTextView.setTextColor(
            ContextCompat.getColor(this, R.color.primary)
        )
        loadIpAddress()
    }

    private fun inflateNodeInfo() {
        binding.nodeType.text = proposalModel.nodeType.nodeType
        binding.nodeProvider.text = proposalModel.providerID
        binding.pricePerHour.text = getString(
            R.string.manual_connect_price_per_hour,
            proposalModel.payment.rate.perSeconds / 3600.0
        )
        binding.pricePerGigabyte.text = getString(
            R.string.manual_connect_price_per_gigabyte,
            proposalModel.payment.rate.perBytes / 1024.0 / 1024.0
        )
    }

    private fun inflateConnectingCardView() {
        binding.selectAnotherNodeButton.visibility = View.VISIBLE
        binding.connectedNodeInfo.visibility = View.INVISIBLE
        binding.titleTextView.text = getString(R.string.manual_connect_disconnected)
        binding.securityStatusTextView.visibility = View.INVISIBLE
        binding.securityStatusImageView.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.short_divider)
        )
        binding.connectionTypeTextView.text = getString(R.string.manual_connect_country_status)
        binding.connectionTypeTextView.setTextColor(
            ContextCompat.getColor(this, R.color.primary)
        )
        binding.connectionState.showConnectionState(proposalModel)
    }

    private fun inflateConnectedCardView() {
        binding.connectionState.showConnectedState()
        binding.connectedNodeInfo.visibility = View.VISIBLE
        binding.titleTextView.text = getString(R.string.manual_connect_connected)
        binding.connectionTypeTextView.text = proposalModel.countryName
        binding.securityStatusTextView.visibility = View.VISIBLE
        binding.securityStatusImageView.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.shape_connected_status)
        )
        binding.manualConnectToolbar.setRightIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_save)
        )
        binding.connectionTypeTextView.setTextColor(
            ContextCompat.getColor(this, R.color.ColorWhite)
        )
    }

    private fun navigateToSelectNode() {
        startActivity(Intent(this, SelectNodeActivity::class.java))
    }
}
