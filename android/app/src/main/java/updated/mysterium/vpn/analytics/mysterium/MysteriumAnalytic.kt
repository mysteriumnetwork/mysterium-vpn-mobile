package updated.mysterium.vpn.analytics.mysterium

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import mysterium.GetBalanceRequest
import updated.mysterium.vpn.analytics.AnalyticEvent
import updated.mysterium.vpn.analytics.AnalyticWrapper
import updated.mysterium.vpn.common.data.DeviceUtil
import updated.mysterium.vpn.model.analytics.ClientAnalyticRequest
import updated.mysterium.vpn.model.analytics.ClientInfo
import updated.mysterium.vpn.model.analytics.EventAnalyticRequest
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class MysteriumAnalytic(
    context: Context,
    private val analyticWrapper: AnalyticWrapper,
    useCaseProvider: UseCaseProvider
) {

    private companion object {
        const val TAG = "MysteriumAnalytic"
    }

    val eventTracked: SharedFlow<String>
        get() = _eventTracked
    private val _eventTracked = MutableSharedFlow<String>()

    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private var balanceRequest: GetBalanceRequest? = null

    private val machineID = DeviceUtil.getDeviceID(context.contentResolver)
    private val appVersion = DeviceUtil.getAppVersion(context)
    private val osVersion = DeviceUtil.getAndroidVersion()
    private val country = DeviceUtil.getConfiguredCountry(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun trackEvent(
        eventName: String,
        pageTitle: String? = null,
        proposal: Proposal? = null
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _eventTracked.tryEmit(eventName)
        }

        scope.launch(handler) {
            requestEventTracking(eventName, pageTitle, proposal)
        }
    }

    private suspend fun requestEventTracking(
        eventName: String,
        pageTitle: String? = null,
        proposal: Proposal? = null
    ) {
        if (eventName == AnalyticEvent.STARTUP.eventName) {
            val clientInfo = getClientInfo()
            val clientAnalyticRequest = ClientAnalyticRequest(
                eventName,
                clientInfo
            )
            analyticWrapper.trackEvent(clientAnalyticRequest)
            _eventTracked.emit(eventName)
        } else {
            val analyticRequest = getAnalyticRequest(
                eventName, proposal, pageTitle
            )
            analyticWrapper.trackEvent(analyticRequest)
            _eventTracked.emit(eventName)
        }
    }

    private fun getClientInfo(): ClientInfo {
        val identityAddress = connectionUseCase.getSavedIdentityAddress()
        return ClientInfo(
            machineID = machineID,
            appVersion = appVersion,
            osVersion = osVersion,
            country = country,
            consumerID = identityAddress
        )
    }

    private suspend fun getAnalyticRequest(
        eventName: String,
        proposal: Proposal?,
        pageTitle: String?
    ): EventAnalyticRequest {

        // client
        val clientInfo = getClientInfo()

        // duration
        var duration: Long? = connectionUseCase.getDuration()
        if (duration == 0L) {
            duration = null
        }

        // balance
        initBalanceRequest()
        var balance: Double? = null
        balanceRequest?.let {
            balance = balanceUseCase.getBalance(it)
        }

        // country, providerID
        val country = proposal?.countryName
        val providerID = proposal?.providerID

        return EventAnalyticRequest(
            eventName = eventName,
            duration = duration,
            balance = balance,
            country = country,
            providerID = providerID,
            pageTitle = pageTitle,
            clientInfo = clientInfo
        )
    }

    private suspend fun initBalanceRequest() {
        val nodeIdentity = connectionUseCase.getIdentity()
        val identity = IdentityModel(
            address = nodeIdentity.address,
            channelAddress = nodeIdentity.channelAddress,
            status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
        )
        balanceRequest = GetBalanceRequest().apply {
            identityAddress = identity.address
        }
    }
}
