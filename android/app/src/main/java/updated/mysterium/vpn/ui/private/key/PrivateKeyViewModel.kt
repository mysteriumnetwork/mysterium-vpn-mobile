package updated.mysterium.vpn.ui.private.key

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PrivateKeyViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val privateKeyUseCase = useCaseProvider.privateKey()
    private val connectionUseCase = useCaseProvider.connection()

    fun downloadKey(passphrase: String) = liveDataResult {
        privateKeyUseCase.downloadPrivateKey(
            connectionUseCase.getIdentityAddress(),
            passphrase
        )
    }

    fun exportIdentity(newPassphrase: String) = liveDataResult {
        val address = connectionUseCase.getIdentityAddress()
        privateKeyUseCase.exportIdentity(address, newPassphrase)
    }
}
