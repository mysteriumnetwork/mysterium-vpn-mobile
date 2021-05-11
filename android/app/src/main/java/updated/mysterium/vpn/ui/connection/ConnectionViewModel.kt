package updated.mysterium.vpn.ui.connection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import mysterium.ConnectRequest
import mysterium.GetBalanceRequest
import network.mysterium.AppNotificationManager
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.Statistics
import network.mysterium.ui.StatisticsModel
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.ConnectionStatistic
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.*

class ConnectionViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val DEFAULT_DNS_OPTION = "auto"
        const val TAG = "HomeViewModel"
    }

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val statisticsUpdate: LiveData<ConnectionStatistic>
        get() = _statisticsUpdate

    val connectionException: LiveData<Exception>
        get() = _connectionException

    val pushDisconnect: LiveData<Unit>
        get() = _pushDisconnect

    val manualDisconnect: LiveData<Unit>
        get() = _manualDisconnect

    private lateinit var proposal: Proposal
    private lateinit var appNotificationManager: AppNotificationManager
    private var coreService: MysteriumCoreService? = null
    private val _connectionException = MutableLiveData<Exception>()
    private val _manualDisconnect = MutableLiveData<Unit>()
    private val _pushDisconnect = MutableLiveData<Unit>()
    private val _statisticsUpdate = MutableLiveData<ConnectionStatistic>()
    private val _connectionState = MutableLiveData<ConnectionState>()
    private val nodesUseCase = useCaseProvider.nodes()
    private val locationUseCase = useCaseProvider.location()
    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val settingsUseCase = useCaseProvider.settings()
    private val deferredNode = DeferredNode()
    private var identity: IdentityModel? = null
    private var exchangeRate: Double? = null
    private var isConnectionStopped = false

    fun init(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
        notificationManager: AppNotificationManager,
        proposal: Proposal
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            appNotificationManager = notificationManager
            coreService = deferredMysteriumCoreService.await()
            startDeferredNode()
            connectNode(proposal)
        }
    }

    fun connectNode(proposal: Proposal) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
            if (!isConnectionStopped && _connectionState.value != ConnectionState.CONNECTED) {
                _connectionException.postValue(exception as Exception)
            }
            isConnectionStopped = false
        }
        viewModelScope.launch(handler) {
            this@ConnectionViewModel.proposal = proposal
            disconnectIfConnectedNode()
            connect()
            getExchangeRate()
        }
    }

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }

    fun addToFavourite(proposal: Proposal) = liveDataResult {
        nodesUseCase.addToFavourite(proposal)
    }

    fun deleteFromFavourite(proposal: Proposal) {
        viewModelScope.launch {
            nodesUseCase.deleteFromFavourite(proposal)
        }
    }

    fun disconnect(isPushDisconnect: Boolean = false) {
        viewModelScope.launch {
            disconnectIfConnectedNode()
            if (isPushDisconnect) {
                _pushDisconnect.postValue(Unit)
            }
        }
    }

    fun stopConnecting() = liveDataResult {
        isConnectionStopped = true
        disconnectNode()
    }

    fun updateCurrentConnectionStatus() = liveDataResult {
        val status = connectionUseCase.status()
        val connectionModel = ConnectionState.valueOf(status.state.toUpperCase(Locale.ROOT))
        _connectionState.postValue(connectionModel)
        connectionModel
    }

    fun getProposalModel(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) = liveDataResult {
        coreService = deferredMysteriumCoreService.await()
        deferredMysteriumCoreService.await().getActiveProposal()
    }

    fun isFavourite(nodeId: String) = liveDataResult {
        nodesUseCase.isFavourite(nodeId)
    }

    fun manualDisconnect() {
        coreService?.manualDisconnect()
    }

    fun getBalance() = liveDataResult {
        val balanceRequest = GetBalanceRequest().apply {
            identityAddress = identity?.address
        }
        balanceUseCase.getBalance(balanceRequest)
    }

    private suspend fun startDeferredNode() {
        if (!deferredNode.startedOrStarting()) {
            coreService?.let {
                deferredNode.start(it)
            }
        }
        connectionUseCase.initDeferredNode(deferredNode)
        locationUseCase.initDeferredNode(deferredNode)
        nodesUseCase.initDeferredNode(deferredNode)
        initListeners()
        loadIdentity()
    }

    private suspend fun initListeners() {
        connectionUseCase.registerStatisticsChangeCallback {
            updateStatistic(it)
        }
        connectionUseCase.connectionStatusCallback {
            val connectionStateModel = ConnectionState.valueOf(it.toUpperCase(Locale.ROOT))
            if (connectionStateModel == ConnectionState.NOTCONNECTED) {
                coreService?.setDeferredNode(null)
                coreService?.setActiveProposal(null)
                coreService?.stopForeground()
            }
            _connectionState.postValue(connectionStateModel)
        }
    }

    private fun updateStatistic(statisticsCallback: Statistics) {
        val statistics = StatisticsModel.from(statisticsCallback)
        val connectionStatistic = ConnectionStatistic(
            duration = statistics.duration,
            bytesReceived = statistics.bytesReceived,
            bytesSent = statistics.bytesSent,
            tokensSpent = statistics.tokensSpent,
            currencySpent = (exchangeRate ?: 0.0) * statistics.tokensSpent
        )
        _statisticsUpdate.postValue(connectionStatistic)
    }

    private suspend fun loadIdentity() {
        connectionUseCase.getIdentity().let {
            identity = IdentityModel(
                it.address,
                it.channelAddress,
                IdentityRegistrationStatus.parse(it.registrationStatus)
            )
        }
    }

    private suspend fun connect() {
        val req = ConnectRequest().apply {
            identityAddress = identity?.address ?: ""
            providerID = proposal.providerID
            serviceType = proposal.serviceType.type
            dnsOption = settingsUseCase.getSavedDns() ?: DEFAULT_DNS_OPTION
        }
        connectionUseCase.connect(req)
        updateService()
    }

    private suspend fun getExchangeRate() {
        exchangeRate = balanceUseCase.getUsdEquivalent()
    }

    private fun updateService() {
        coreService?.apply {
            setDeferredNode(deferredNode)
            setActiveProposal(ProposalViewItem.parse(proposal))
            startForegroundWithNotification(
                appNotificationManager.statisticsNotification,
                appNotificationManager.createConnectedToVPNNotification()
            )
        }
    }

    private suspend fun disconnectIfConnectedNode() {
        if (_connectionState.value == ConnectionState.CONNECTED) {
            disconnectNode()
        }
    }

    private suspend fun disconnectNode() {
        coreService?.manualDisconnect()
        _manualDisconnect.postValue(Unit)
        connectionUseCase.disconnect()
        coreService?.setActiveProposal(null)
        coreService?.setDeferredNode(null)
        coreService?.stopForeground()
    }
}
