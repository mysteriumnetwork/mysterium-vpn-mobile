package updated.mysterium.vpn.ui.connection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityHomeBinding
import network.mysterium.vpn.databinding.PopUpLostConnectionBinding
import network.mysterium.vpn.databinding.PopUpNodeFailedBinding
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.injectOrNull
import updated.mysterium.vpn.App
import updated.mysterium.vpn.analytics.AnalyticEvent
import updated.mysterium.vpn.analytics.mysterium.MysteriumAnalytic
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.getTypeLabelResource
import updated.mysterium.vpn.common.playstore.PlayStoreHelper
import updated.mysterium.vpn.exceptions.ConnectAlreadyExistsException
import updated.mysterium.vpn.exceptions.ConnectInsufficientBalanceException
import updated.mysterium.vpn.model.connection.ConnectionType
import updated.mysterium.vpn.model.connection.Status
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.nodes.list.FilterActivity
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class ConnectionActivity : BaseActivity() {

    companion object {
        const val EXTRA_PROPOSAL_MODEL = "PROPOSAL_MODEL"
        const val CONNECTION_TYPE_KEY = "CONNECTION_TYPE"
        const val COUNTRY_CODE_KEY = "COUNTRY_CODE"
        private const val CURRENCY = "MYSTT"
        private const val MIN_BALANCE = 0.0001
    }

    private lateinit var binding: ActivityHomeBinding
    private var proposal: Proposal? = null
    private var connectionType: ConnectionType? = null
    private val viewModel: ConnectionViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val analytic: MysteriumAnalytic by inject()
    private val notificationManager: AppNotificationManager by inject()
    private var isDisconnectedByUser = false

    private val playStoreHelper: PlayStoreHelper? by injectOrNull(PlayStoreHelper::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        getSelectedNode()
    }

    override fun onResume() {
        super.onResume()
        subscribeConnectionListener()
        checkCurrentStatus()
        checkAbilityToConnect()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        getSelectedNode()
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
        viewModel.statisticsUpdate.observe(this) {
            binding.connectionState.updateConnectedStatistics(it, CURRENCY)
        }
        viewModel.connectionException.observe(this) {
            if (viewModel.connectionStatus.value?.state != ConnectionState.CONNECTED) {
                analytic.trackEvent(
                    eventName = AnalyticEvent.CONNECT_FAILURE.eventName,
                    proposal = proposal
                )
                Log.e(TAG, it.localizedMessage ?: it.toString())
                if (it is ConnectInsufficientBalanceException) {
                    disconnect()
                    insufficientFundsPopUp {
                        navigateBack()
                    }
                } else if (it !is ConnectAlreadyExistsException) {
                    disconnect()
                    failedToConnect()
                }
            }
        }
        viewModel.manualDisconnect.observe(this) {
            manualDisconnecting()
        }
        viewModel.pushDisconnect.observe(this) {
            navigateBack()
        }
        viewModel.successConnectEvent.observe(this) {
            proposal = it
            analytic.trackEvent(
                eventName = AnalyticEvent.CONNECT_SUCCESS.eventName,
                proposal = proposal
            )
        }
    }

    private fun subscribeConnectionListener() {
        viewModel.connectionStatus.observe(this) {
            proposal = it.proposal
            handleConnectionChange(it)
        }
    }

    private fun getSelectedNode() {
        if (isInternetAvailable) {
            checkProposalArgument()
        } else {
            wifiNetworkErrorPopUp {
                baseViewModel.checkInternetConnection()
            }
        }
    }

    private fun initViewModel(
        connectionType: ConnectionType?,
        countryCode: String?,
        proposal: Proposal?
    ) {
        analytic.trackEvent(
            eventName = AnalyticEvent.CONNECT_ATTEMPT.eventName,
            proposal = proposal
        )
        viewModel.init(
            deferredMysteriumCoreService = App.getInstance(this).deferredMysteriumCoreService,
            notificationManager = notificationManager,
            connectionType = connectionType,
            countryCode = countryCode,
            proposal = proposal,
            rate = exchangeRateViewModel.usdEquivalent
        )
    }

    private fun manualDisconnecting() {
        isDisconnectedByUser = true
        viewModel.manualDisconnect()
    }

    private fun handleConnectionChange(status: Status) {
        when (status.state) {
            ConnectionState.NOTCONNECTED -> {
                loadIpAddress()
                viewModel.clearDuration()
            }
            ConnectionState.CONNECTING -> {
                inflateConnectingCardView()
            }
            ConnectionState.CONNECTED -> {
                loadIpAddress()
                inflateConnectedCardView()
                checkForReview()
            }
            ConnectionState.DISCONNECTING -> {
                showDisconnectingState()
                checkDisconnectingReason()
            }
            ConnectionState.ON_HOLD -> {
                loadIpAddress()
                inflateConnectedCardView()
                viewModel.repeatLastConnection()
            }
        }
        updateStatusTitle(connectionState)
    }

    private fun checkForReview() {
        viewModel.isReviewAvailable().observe(this) {
            it.onSuccess { isReviewAvailable ->
                if (isReviewAvailable) {
                    playStoreHelper?.showReview(this)
                }
            }
        }
    }

    private fun checkAbilityToConnect() {
        viewModel.identity?.let { identity ->
            viewModel.getBalance(identity.address).observe(this) {
                it.onSuccess { balance ->
                    if (balance == 0.0) {
                        insufficientFundsPopUp {
                            manualDisconnecting()
                            viewModel.disconnect()
                            navigateBack()
                        }
                    }
                }
            }
        }
    }

    private fun checkCurrentStatus() {
        viewModel.updateCurrentConnectionStatus().observe(this) { result ->
            result.onSuccess {
                updateStatusTitle(it.state)
                if (
                    it.state == ConnectionState.CONNECTED ||
                    it.state == ConnectionState.CONNECTING ||
                    it.state == ConnectionState.ON_HOLD
                ) {
                    getProposal()
                    toolbarSaveIcon()
                }
            }
            result.onFailure { throwable ->
                navigateBack()
                Log.i(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        }
    }

    private fun updateStatusTitle(connectionState: ConnectionState) {
        when (connectionState) {
            ConnectionState.CONNECTING -> {
                binding.titleTextView.text = getString(R.string.manual_connect_connecting)
            }
            ConnectionState.CONNECTED -> {
                binding.titleTextView.text = getString(R.string.manual_connect_connected)
            }
            ConnectionState.ON_HOLD -> {
                binding.titleTextView.text = getString(R.string.manual_connect_on_hold)
            }
            ConnectionState.DISCONNECTING -> {
                binding.titleTextView.text = getString(R.string.manual_connect_disconnecting)
            }
        }
    }

    private fun checkDisconnectingReason() {
        if (!isDisconnectedByUser) {
            if (isInternetAvailable) {
                showLostConnectionPopUp()
            } else {
                wifiNetworkErrorPopUp {
                    baseViewModel.checkInternetConnection()
                }
            }
        }
    }

    private fun getProposal() {
        viewModel.connectionStatus.value?.proposal?.let {
            this.proposal = it
            inflateNodeInfo()
        }
    }

    private fun checkProposalArgument() {
        intent?.extras?.getString(CONNECTION_TYPE_KEY)?.let {
            connectionType = ConnectionType.from(it)
            val countryCode = intent?.extras?.getString(COUNTRY_CODE_KEY)
            val proposalExtra = intent.extras?.getParcelable<Proposal>(EXTRA_PROPOSAL_MODEL)
            if (viewModel.connectionStatus.value?.state != ConnectionState.CONNECTED) {
                proposal = proposalExtra
                initViewModel(connectionType, countryCode, proposalExtra)
            }
            manualDisconnecting()
            inflateNodeInfo()
            inflateConnectingCardView()
            if (
                viewModel.connectionStatus.value?.state == ConnectionState.CONNECTED ||
                viewModel.connectionStatus.value?.state == ConnectionState.CONNECTING ||
                viewModel.connectionStatus.value?.state == ConnectionState.ON_HOLD ||
                viewModel.connectionStatus.value?.state == ConnectionState.IP_NOT_CHANGED
            ) {
                manualDisconnecting()
                analytic.trackEvent(
                    eventName = AnalyticEvent.DISCONNECT_ATTEMPT.eventName,
                    proposal = proposal
                )
                viewModel.disconnect().observe(this) {
                    it.onSuccess {
                        analytic.trackEvent(
                            eventName = AnalyticEvent.CONNECT_ATTEMPT.eventName,
                            proposal = proposal
                        )
                        viewModel.connect(
                            connectionType,
                            countryCode,
                            proposalExtra,
                            exchangeRateViewModel.usdEquivalent
                        )
                    }
                    it.onFailure {
                        analytic.trackEvent(
                            eventName = AnalyticEvent.DISCONNECT_FAILURE.eventName,
                            proposal = proposal
                        )
                    }
                }
            }
        }
    }

    private fun loadIpAddress() {
        binding.ipTextView.text = getString(R.string.manual_connect_loading)
        viewModel.getLocation().observe(this) { result ->
            result.onSuccess {
                binding.ipTextView.text = it.ip
            }
            result.onFailure {
                Log.e(TAG, "Data loading failed")
                binding.ipTextView.text = getString(R.string.manual_connect_unknown)
                loadIpAddress()
            }
        }
    }

    private fun bindsAction() {
        binding.cancelConnectionButton.setOnClickListener {
            manualDisconnecting()
            viewModel.stopConnecting().observe(this) { result ->
                result.onFailure {
                    Log.e(TAG, it.localizedMessage ?: it.toString())
                }
                navigateBack()
            }
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
                analytic.trackEvent(
                    eventName = AnalyticEvent.DISCONNECT_ATTEMPT.eventName,
                    proposal = proposal
                )
                viewModel.disconnect().observe(this) {
                    it.onSuccess {
                        navigateBack()
                    }
                    it.onFailure {
                        analytic.trackEvent(
                            eventName = AnalyticEvent.DISCONNECT_FAILURE.eventName,
                            proposal = proposal
                        )
                    }
                }
            }
        )
    }

    private fun toolbarSaveIcon() {
        binding.manualConnectToolbar.onRightButtonClicked {
            proposal?.let { proposal ->
                viewModel.isFavourite(proposal.providerID + proposal.serviceType)
                    .observe(this) { result ->
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
                    }
            }
        }
        isFavourite()
    }

    private fun deleteFromFavourite(proposal: Proposal) {
        viewModel.deleteFromFavourite(proposal)
        binding.manualConnectToolbar.setRightIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_save)
        )
    }

    private fun addToFavourite(proposal: Proposal) {
        viewModel.addToFavourite(proposal).observe(this) { result ->
            result.onSuccess {
                Log.i(TAG, "onSuccess")
            }
            result.onFailure {
                Log.i(TAG, it.localizedMessage ?: it.toString())
            }
        }
        binding.manualConnectToolbar.setRightIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_saved)
        )
    }

    private fun disconnect() {
        loadIpAddress()
        isDisconnectedByUser = false
    }

    private fun inflateNodeInfo() {
        proposal?.let {
            binding.nodeType.text = getString(it.nodeType.getTypeLabelResource())
            binding.nodeProvider.text = it.providerID
            // convert seconds to hours
            binding.pricePerHour.text = getString(
                R.string.manual_connect_price_per_hour,
                it.payment.perHour
            )
            // convert price by bytes to price by gigabytes
            binding.pricePerGigabyte.text = getString(
                R.string.manual_connect_price_per_gigabyte,
                it.payment.perGib
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
        binding.connectionCountryTextView.visibility = View.INVISIBLE
        binding.manualConnectToolbar.setRightIcon(null)
        binding.connectionState.showConnectionState(connectionType, proposal)
        binding.multiAnimation.connectingState()
    }

    private fun inflateConnectedCardView() {
        toolbarSaveIcon()
        binding.cancelConnectionButton.visibility = View.INVISIBLE
        binding.selectAnotherNodeButton.visibility = View.VISIBLE
        binding.connectionState.showConnectedState()
        binding.connectedNodeInfo.visibility = View.VISIBLE
        binding.titleTextView.text = getString(R.string.manual_connect_connected)
        binding.connectionCountryTextView.visibility = View.VISIBLE
        binding.connectionCountryTextView.text = proposal?.countryName ?: "UNKNOWN"
        binding.connectionCountryTextView.setTextColor(
            ContextCompat.getColor(this, R.color.ColorWhite)
        )
        binding.securityStatusImageView.visibility = View.INVISIBLE
        binding.connectedStatusImageView.visibility = View.VISIBLE
        binding.multiAnimation.connectedState()
    }

    private fun showDisconnectingState() {
        binding.connectionCountryTextView.text = proposal?.countryName ?: "UNKNOWN"
        binding.connectedStatusImageView.visibility = View.INVISIBLE
        binding.connectionState.showDisconnectingState()
    }

    private fun isFavourite() {
        proposal?.let {
            viewModel.isFavourite(it.providerID + it.serviceType).observe(this) { result ->
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
            }
        }
    }

    private fun failedToConnect() {
        viewModel.getBalance().observe(this) {
            it.onSuccess { balance ->
                if (balance < MIN_BALANCE) {
                    insufficientFundsPopUp {
                        manualDisconnecting()
                        viewModel.disconnect()
                        navigateBack()
                    }
                } else {
                    showFailedToConnectPopUp()
                }
            }
        }
    }

    private fun showFailedToConnectPopUp() {
        val bindingPopUp = PopUpNodeFailedBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.chooseAnother.setOnClickListener {
            dialog.dismiss()
            navigateBack()
        }
        dialog.show()
    }

    private fun showLostConnectionPopUp() {
        val bindingPopUp = PopUpLostConnectionBinding.inflate(layoutInflater)
        val lostConnectionPopUpDialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.closeButton.setOnClickListener {
            lostConnectionPopUpDialog.dismiss()
        }
        lostConnectionPopUpDialog.show()
    }

    private fun navigateBack() {
        if (connectionType == ConnectionType.SMART_CONNECT) {
            navigateToSelectNode(true)
        } else {
            backToFilter()
        }
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

    private fun backToFilter() {
        val intent = Intent(this, FilterActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
