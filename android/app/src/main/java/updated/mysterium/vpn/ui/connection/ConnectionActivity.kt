package updated.mysterium.vpn.ui.connection

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import network.mysterium.AppNotificationManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityHomeBinding
import network.mysterium.vpn.databinding.PopUpLostConnectionBinding
import network.mysterium.vpn.databinding.PopUpNodeFailedBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.App
import updated.mysterium.vpn.common.extensions.getTypeLabelResource
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.favourites.FavouritesActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.menu.MenuActivity

class ConnectionActivity : BaseActivity() {

    companion object {
        const val EXTRA_PROPOSAL_MODEL = "PROPOSAL_MODEL"
        private const val TAG = "HomeActivity"
        private const val CURRENCY = "MYSTT"
        private const val SECONDS_PER_HOUR = 3600.0
        private const val BYTES_PER_GIGABYTE = 1024.0 * 1024.0 * 1024.0
        private const val MIN_BALANCE = 0.0001
    }

    private lateinit var binding: ActivityHomeBinding
    private var proposal: Proposal? = null
    private val viewModel: ConnectionViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private var isDisconnectedByUser = false
    private var lostConnectionPopUpDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
    }

    override fun onResume() {
        super.onResume()
        subscribeConnectionListener()
        checkCurrentStatus()
        checkAbilityToConnect()
        allNodesViewModel.initProposals()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (isInternetAvailable) {
            setIntent(intent)
            intent?.extras?.getParcelable<Proposal>(EXTRA_PROPOSAL_MODEL)?.let {
                proposal = it
                manualDisconnecting()
                inflateNodeInfo()
                inflateConnectingCardView()
                viewModel.connectNode(it)
            }
        } else {
            wifiNetworkErrorPopUp()
        }
    }

    override fun retryLoading() {
        getSelectedNode()
    }

    override fun protectedConnection() {
        connectionStateToolbar?.protectedState(false)
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        loadIpAddress()
    }

    private fun subscribeViewModel() {
        subscribeConnectionListener()
        viewModel.statisticsUpdate.observe(this, {
            binding.connectionState.updateConnectedStatistics(it, CURRENCY)
        })
        viewModel.connectionException.observe(this, {
            if (viewModel.connectionState.value != ConnectionState.CONNECTED) {
                Log.e(TAG, it.localizedMessage ?: it.toString())
                disconnect()
                failedToConnect()
            }
        })
        viewModel.manualDisconnect.observe(this, {
            manualDisconnecting()
        })
        viewModel.pushDisconnect.observe(this, {
            navigateToSelectNode(true)
        })
    }

    private fun subscribeConnectionListener() {
        viewModel.connectionState.observe(this, {
            handleConnectionChange(it)
        })
    }

    private fun getSelectedNode() {
        if (isInternetAvailable) {
            checkProposalArgument()
        } else {
            wifiNetworkErrorPopUp()
        }
    }

    private fun initViewModel(proposal: Proposal) {
        viewModel.init(
            deferredMysteriumCoreService = App.getInstance(this).deferredMysteriumCoreService,
            notificationManager = AppNotificationManager(
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            ).apply { init(this@ConnectionActivity) },
            proposal = proposal
        )
    }

    private fun manualDisconnecting() {
        isDisconnectedByUser = true
        viewModel.manualDisconnect()
    }

    private fun handleConnectionChange(connection: ConnectionState) {
        when (connection) {
            ConnectionState.NOTCONNECTED -> disconnect()
            ConnectionState.CONNECTING -> inflateConnectingCardView()
            ConnectionState.CONNECTED -> {
                lostConnectionPopUpDialog?.dismiss()
                isDisconnectedByUser = false
                loadIpAddress()
                inflateConnectedCardView()
            }
            ConnectionState.DISCONNECTING -> {
                binding.connectionState.showDisconnectingState()
                checkDisconnectingReason()
            }
            ConnectionState.ON_HOLD -> {
                showLostConnectionPopUp()
            }
        }
    }

    private fun checkAbilityToConnect() {
        viewModel.getBalance().observe(this, {
            it.onSuccess { balance ->
                if (balance == 0.0) {
                    insufficientFundsPopUp {
                        manualDisconnecting()
                        viewModel.disconnect()
                        navigateToSelectNode(true)
                    }
                }
            }
        })
    }

    private fun checkCurrentStatus() {
        viewModel.updateCurrentConnectionStatus().observe(this, { result ->
            result.onSuccess {
                if (it == ConnectionState.CONNECTED || it == ConnectionState.CONNECTING) {
                    getProposal()
                }
            }
            result.onFailure { throwable ->
                navigateToSelectNode(true)
                Log.i(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        })
    }

    private fun checkDisconnectingReason() {
        if (!isDisconnectedByUser) {
            if (isInternetAvailable) {
                showLostConnectionPopUp()
            } else {
                wifiNetworkErrorPopUp()
            }
        }
    }

    private fun getProposal() {
        viewModel.proposal?.let {
            this.proposal = it
            inflateNodeInfo()
        }
    }

    private fun checkProposalArgument() {
        intent.extras?.getParcelable<Proposal>(EXTRA_PROPOSAL_MODEL)?.let {
            manualDisconnecting()
            proposal = it
            initViewModel(it)
            inflateNodeInfo()
            inflateConnectingCardView()
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
        binding.cancelConnectionButton.setOnClickListener {
            manualDisconnecting()
            viewModel.stopConnecting().observe(this, { result ->
                result.onFailure {
                    Log.e(TAG, it.localizedMessage ?: it.toString())
                }
                navigateToSelectNode(clearTasks = true)
            })
        }
        binding.selectAnotherNodeButton.setOnClickListener {
            navigateToSelectNode()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            navigateToMenu()
        }
        binding.connectionState.initListeners(
            disconnect = {
                manualDisconnecting()
                viewModel.disconnect()
                navigateToSelectNode(true)
            }
        )
    }

    private fun toolbarSaveIcon() {
        binding.manualConnectToolbar.onRightButtonClicked {
            proposal?.let { proposal ->
                viewModel.isFavourite(proposal.providerID + proposal.serviceType).observe(this, { result ->
                    result.onSuccess {
                        if (it != null) {
                            deleteFromFavourite(proposal)
                        } else {
                            addToFavourite(proposal)
                        }
                    }
                    result.onFailure {
                        Log.i(TAG, it.localizedMessage ?: it.toString())
                    }
                })
            }
        }
        isFavourite()
    }

    private fun toolbarWalletIcon() {
        binding.manualConnectToolbar.onRightButtonClicked {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
        binding.manualConnectToolbar.setRightIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_favourites)
        )
    }

    private fun deleteFromFavourite(proposal: Proposal) {
        viewModel.deleteFromFavourite(proposal)
        binding.manualConnectToolbar.setRightIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_save)
        )
    }

    private fun addToFavourite(proposal: Proposal) {
        viewModel.addToFavourite(proposal).observe(this, { result ->
            result.onSuccess {
                Log.i(TAG, "onSuccess")
            }
            result.onFailure {
                Log.i(TAG, it.localizedMessage ?: it.toString())
            }
        })
        binding.manualConnectToolbar.setRightIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_saved)
        )
    }

    private fun disconnect() {
        loadIpAddress()
        toolbarWalletIcon()
        isDisconnectedByUser = false
    }

    private fun inflateNodeInfo() {
        proposal?.let {
            binding.nodeType.text = getString(it.nodeType.getTypeLabelResource())
            binding.nodeProvider.text = it.providerID
            // convert seconds to hours
            binding.pricePerHour.text = getString(
                R.string.manual_connect_price_per_hour,
                it.payment.rate.perSeconds / SECONDS_PER_HOUR
            )
            // convert price by bytes to price by gigabytes
            binding.pricePerGigabyte.text = getString(
                R.string.manual_connect_price_per_gigabyte,
                it.payment.rate.perBytes / BYTES_PER_GIGABYTE
            )
        }
    }

    private fun inflateConnectingCardView() {
        toolbarSaveIcon()
        binding.selectAnotherNodeButton.visibility = View.INVISIBLE
        binding.cancelConnectionButton.visibility = View.VISIBLE
        binding.connectedNodeInfo.visibility = View.INVISIBLE
        binding.titleTextView.text = getString(R.string.manual_connect_connecting)
        binding.securityStatusImageView.visibility = View.VISIBLE
        binding.connectedStatusImageView.visibility = View.INVISIBLE
        binding.connectionTypeTextView.visibility = View.INVISIBLE
        binding.manualConnectToolbar.setRightIcon(null)
        proposal?.let {
            binding.connectionState.showConnectionState(it)
        }
        binding.multiAnimation.connectingState()
    }

    private fun inflateConnectedCardView() {
        toolbarSaveIcon()
        binding.cancelConnectionButton.visibility = View.INVISIBLE
        binding.selectAnotherNodeButton.visibility = View.VISIBLE
        binding.connectionState.showConnectedState()
        binding.connectedNodeInfo.visibility = View.VISIBLE
        binding.titleTextView.text = getString(R.string.manual_connect_connected)
        binding.connectionTypeTextView.visibility = View.VISIBLE
        binding.connectionTypeTextView.text = proposal?.countryName ?: "UNKNOWN"
        binding.securityStatusImageView.visibility = View.INVISIBLE
        binding.connectedStatusImageView.visibility = View.VISIBLE
        binding.connectionTypeTextView.setTextColor(
            ContextCompat.getColor(this, R.color.ColorWhite)
        )
        binding.multiAnimation.connectedState()
    }

    private fun isFavourite() {
        proposal?.let {
            viewModel.isFavourite(it.providerID + it.serviceType).observe(this, { result ->
                result.onSuccess { nodeEntity ->
                    if (nodeEntity != null) {
                        binding.manualConnectToolbar.setRightIcon(
                            ContextCompat.getDrawable(this, R.drawable.icon_saved)
                        )
                    } else {
                        binding.manualConnectToolbar.setRightIcon(
                            ContextCompat.getDrawable(this, R.drawable.icon_save)
                        )
                    }
                }
            })
        }
    }

    private fun failedToConnect() {
        viewModel.getBalance().observe(this, {
            it.onSuccess { balance ->
                if (balance < MIN_BALANCE) {
                    insufficientFundsPopUp {
                        manualDisconnecting()
                        viewModel.disconnect()
                        navigateToSelectNode(true)
                    }
                } else {
                    showFailedToConnectPopUp()
                }
            }
        })
    }

    private fun showFailedToConnectPopUp() {
        val bindingPopUp = PopUpNodeFailedBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.chooseAnother.setOnClickListener {
            dialog.dismiss()
            navigateToSelectNode()
        }
        dialog.show()
    }

    private fun showLostConnectionPopUp() {
        val bindingPopUp = PopUpLostConnectionBinding.inflate(layoutInflater)
        lostConnectionPopUpDialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.closeButton.setOnClickListener {
            lostConnectionPopUpDialog?.dismiss()
        }
        lostConnectionPopUpDialog?.show()
    }

    private fun navigateToSelectNode(clearTasks: Boolean = false) {
        val intent = Intent(this, HomeSelectionActivity::class.java)
        if (clearTasks) {
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        startActivity(intent)
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
