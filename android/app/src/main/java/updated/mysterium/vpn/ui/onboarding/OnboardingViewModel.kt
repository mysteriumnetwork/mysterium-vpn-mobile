package updated.mysterium.vpn.ui.onboarding

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class OnboardingViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()

    fun userLoggedIn() {
        loginUseCase.userLoggedIn()
    }
}
