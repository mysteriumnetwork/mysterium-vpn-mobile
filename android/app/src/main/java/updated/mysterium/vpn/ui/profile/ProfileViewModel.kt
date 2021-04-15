package updated.mysterium.vpn.ui.profile

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ProfileViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val connectionUseCase = useCaseProvider.connection()
    private val privateKeyUseCase = useCaseProvider.privateKey()

    fun getIdentity() = liveDataResult {
        val identity = connectionUseCase.getIdentity()
        identity
    }

    fun downloadKey(passphrase: String) = liveDataResult {
        privateKeyUseCase.downloadPrivateKey(
            connectionUseCase.getIdentityAddress(),
            passphrase
        )
    }
}
