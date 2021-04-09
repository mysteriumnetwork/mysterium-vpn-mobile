package updated.mysterium.vpn.ui.create.account

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class CreateAccountViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val privateKeyUseCase = useCaseProvider.privateKey()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()

    fun importAccount(privateKey: String, passphrase: String) = liveDataResult {
        privateKeyUseCase.importIdentity(privateKey, passphrase)
    }

    fun applyNewIdentity(importedIdentity: String) = liveDataResult {
        connectionUseCase.getNewIdentity(importedIdentity)
    }
}
