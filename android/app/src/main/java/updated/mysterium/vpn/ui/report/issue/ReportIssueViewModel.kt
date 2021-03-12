package updated.mysterium.vpn.ui.report.issue

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ReportIssueViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val reportUseCase = useCaseProvider.report()

    fun sendReport(email: String, content: String) = liveDataResult {
        reportUseCase.sendReport(email, content)
    }
}
