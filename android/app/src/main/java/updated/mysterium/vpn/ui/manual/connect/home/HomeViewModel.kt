package updated.mysterium.vpn.ui.manual.connect.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import mysterium.ConnectRequest
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

class HomeViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "HomeViewModel"
        const val DEFAULT_DNS_OPTION = "auto"
        const val UNSTABLE_DNS_OPTION = "cloudflare"
    }

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val statisticsUpdate: LiveData<ConnectionStatistic>
        get() = _statisticsUpdate

    val connectionException: LiveData<Exception>
        get() = _connectionException

    private lateinit var proposal: Proposal
    private lateinit var appNotificationManager: AppNotificationManager
    private lateinit var coreService: MysteriumCoreService
    private val _connectionException = MutableLiveData<Exception>()
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

    fun init(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
        notificationManager: AppNotificationManager
    ) {
        viewModelScope.launch {
            appNotificationManager = notificationManager
            coreService = deferredMysteriumCoreService.await()
            startDeferredNode()
        }
    }

    fun connectNode(proposal: Proposal) {
        val handler = CoroutineExceptionHandler { _, exception ->
            _connectionException.postValue(exception as Exception)
        }
        viewModelScope.launch(handler) {
            this@HomeViewModel.proposal = proposal
            disconnectNode()
            connect()
            getExchangeRate()
        }
    }

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }

    fun showStatisticsNotification(notificationTitle: String, notificationContent: String) {
        viewModelScope.launch {
            appNotificationManager.showStatisticsNotification(
                notificationTitle, notificationContent
            )
        }
    }

    fun addToFavourite(proposal: Proposal) = liveDataResult {
        nodesUseCase.addToFavourite(proposal)
    }

    fun disconnect() {
        viewModelScope.launch {
            disconnectNode()
        }
    }

    fun updateCurrentConnectionStatus() = liveDataResult {
        val status = connectionUseCase.status()
        val connectionModel = ConnectionState.valueOf(status.state.toUpperCase())
        _connectionState.postValue(connectionModel)
        connectionModel
    }

    fun getProposalModel(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) = liveDataResult {
        coreService = deferredMysteriumCoreService.await()
        deferredMysteriumCoreService.await().getActiveProposal()
    }

    private suspend fun startDeferredNode() {
        if (!deferredNode.startedOrStarting()) {
            deferredNode.start(coreService)
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
            val connectionStateModel = ConnectionState.valueOf(it.toUpperCase())
            if (connectionStateModel == ConnectionState.NOTCONNECTED) {
                coreService.setDeferredNode(null)
                coreService.setActiveProposal(null)
                coreService.stopForeground()
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
        var dnsOption = settingsUseCase.getSavedDns() ?: DEFAULT_DNS_OPTION
        if (dnsOption == UNSTABLE_DNS_OPTION) {
            dnsOption = DEFAULT_DNS_OPTION
        }
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
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to load currency: $exception")
        }
        viewModelScope.launch(handler) {
            exchangeRate = withContext(Dispatchers.Default) {
                balanceUseCase.getUsdEquivalent()
            }
        }
    }

    private fun updateService() {
        coreService.apply {
            setDeferredNode(deferredNode)
            setActiveProposal(
                ProposalViewItem(
                    id = proposal.id,
                    providerID = proposal.providerID,
                    serviceType = proposal.serviceType,
                    countryCode = proposal.countryCode,
                    nodeType = proposal.nodeType,
                    monitoringFailed = proposal.monitoringFailed,
                    payment = proposal.payment
                )
            )
            startForegroundWithNotification(
                appNotificationManager.defaultNotificationID,
                appNotificationManager.createConnectedToVPNNotification()
            )
        }
    }

    private suspend fun disconnectNode() {
        if (_connectionState.value == ConnectionState.CONNECTED) {
            connectionUseCase.disconnect()
            coreService.setActiveProposal(null)
            coreService.setDeferredNode(null)
            coreService.stopForeground()
        }
    }
}
