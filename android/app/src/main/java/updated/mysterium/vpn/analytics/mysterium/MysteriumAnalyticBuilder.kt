package updated.mysterium.vpn.analytics.mysterium

import android.content.Context
import updated.mysterium.vpn.common.data.DeviceUtil
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.analytics.ClientInfo
import updated.mysterium.vpn.model.analytics.EventInfo
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.connection.ConnectionViewModel

class MysteriumAnalyticBuilder(
    private val context: Context,
    private val connectionViewModel: ConnectionViewModel,
    private val balanceViewModel: BalanceViewModel,
    useCaseProvider: UseCaseProvider
) {

    private val connectionUseCase = useCaseProvider.connection()

    fun getClientInfo(eventName: String): ClientInfo {
        val identityAddress = connectionUseCase.getSavedIdentityAddress()
        return ClientInfo(
            eventName = eventName,
            machineID = DeviceUtil.getDeviceID(context.contentResolver),
            appVersion = DeviceUtil.getAppVersion(context),
            osVersion = DeviceUtil.getAndroidVersion(),
            country = DeviceUtil.getConfiguredCountry(context),
            consumerID = identityAddress
        )
    }

    fun getEventInfo(eventName: String, pageTitle: String?) = liveDataResult {
        // duration
        var duration: String? = null
        if (connectionViewModel.connectionState.value == ConnectionState.CONNECTED) {
            connectionViewModel.statisticsUpdate.value?.let {
                duration = it.duration
            }
        }

        // balance
        balanceViewModel.getCurrentBalance().removeObserver {

        }
        val balance: Double? = balanceViewModel.balanceLiveData.value

        // country, providerID
        val country = connectionViewModel.proposal?.countryName
        val providerID = connectionViewModel.proposal?.providerID

        EventInfo(
            eventName = eventName,
            duration = duration,
            balance = balance,
            country = country,
            providerID = providerID,
            pageTitle = pageTitle
        )
    }
}
