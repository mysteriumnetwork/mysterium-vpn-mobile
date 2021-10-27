package updated.mysterium.vpn.network.usecase

import mysterium.ConnectRequest
import mysterium.GetIdentityRequest
import mysterium.RegisterIdentityRequest
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.model.statistics.Statistics
import updated.mysterium.vpn.model.wallet.Identity

class ConnectionUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    fun getSavedIdentityAddress() = sharedPreferencesManager.getStringPreferenceValue(
        SharedPreferencesList.IDENTITY_ADDRESS
    )

    fun setDuration(duration: Long) {
        sharedPreferencesManager.setPreferenceValue(
            SharedPreferencesList.DURATION,
            duration
        )
    }

    fun getDuration() = sharedPreferencesManager.getLongPreferenceValue(
        SharedPreferencesList.DURATION
    )

    fun clearDuration() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.DURATION)
    }

    suspend fun connect(connectRequest: ConnectRequest) = nodeRepository.connect(connectRequest)

    suspend fun getIdentity(): Identity {
        val identity = nodeRepository.getIdentity()
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.IDENTITY_ADDRESS,
            value = identity.address
        )
        return identity
    }

    suspend fun getNewIdentity(newIdentityAddress: String): Identity {
        val getIdentityRequest = GetIdentityRequest().apply {
            address = newIdentityAddress
        }
        val identity = nodeRepository.getIdentity(getIdentityRequest)
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.IDENTITY_ADDRESS,
            value = identity.address
        )
        return identity
    }

    suspend fun getIdentityAddress(): String {
        return getSavedIdentityAddress() ?: nodeRepository.getIdentity().address
    }

    suspend fun registerIdentity(
        registerIdentityRequest: RegisterIdentityRequest
    ) = nodeRepository.registerIdentity(registerIdentityRequest)

    suspend fun registrationFees() = nodeRepository.identityRegistrationFees()

    suspend fun status() = nodeRepository.status()

    suspend fun registerStatisticsChangeCallback(
        callback: (Statistics) -> Unit
    ) = nodeRepository.registerStatisticsChangeCallback(callback)

    suspend fun connectionStatusCallback(
        callback: (String) -> Unit
    ) = nodeRepository.registerConnectionStatusChangeCallback(callback)

    suspend fun disconnect() = nodeRepository.disconnect()
}
