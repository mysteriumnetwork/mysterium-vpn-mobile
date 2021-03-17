package updated.mysterium.vpn.ui.onboarding

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class OnboardingViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()

    fun userLoggedIn() {
        loginUseCase.userLoggedIn()
    }

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()
}
