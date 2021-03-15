package updated.mysterium.vpn.ui.profile

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ProfileViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    // MOCK data - random char set. Should be replaced on user passphrase after implementing it
    private companion object {
        const val PRIVATE_KEY_PASSPHRASE = "fhHGF12G2g"
    }

    private val connectionUseCase = useCaseProvider.connection()
    private val privateKeyUseCase = useCaseProvider.privateKey()
    private var identityAddress: String? = null

    fun getIdentity() = liveDataResult {
        val identity = connectionUseCase.getIdentity()
        identityAddress = identity.address
        identity
    }

    fun downloadKey() = liveDataResult {
        identityAddress?.let {
            privateKeyUseCase.downloadPrivateKey(it, PRIVATE_KEY_PASSPHRASE)
        }
    }
}
