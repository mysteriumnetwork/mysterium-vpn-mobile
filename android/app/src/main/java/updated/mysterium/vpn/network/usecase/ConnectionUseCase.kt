package updated.mysterium.vpn.network.usecase

import mysterium.ConnectRequest
import mysterium.GetIdentityRequest
import mysterium.RegisterIdentityRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.Identity
import network.mysterium.service.core.NodeRepository
import network.mysterium.service.core.Statistics
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class ConnectionUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
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
        val savedAddress = sharedPreferencesManager.getStringPreferenceValue(
            SharedPreferencesList.IDENTITY_ADDRESS
        )
        return savedAddress ?: nodeRepository.getIdentity().address
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
