package updated.mysterium.vpn.network.usecase

import mysterium.SendFeedbackRequest
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.NodeRepository

class ReportUseCase(private val nodeRepository: NodeRepository) {

    private companion object {
        const val REPORT_TEMPLATE = "Platform: Android, Message:"
    }

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun sendReport(email: String, content: String) = nodeRepository.sendFeedback(
        SendFeedbackRequest().apply {
            this.email = email
            this.description = "$REPORT_TEMPLATE $content"
        }
    )
}
