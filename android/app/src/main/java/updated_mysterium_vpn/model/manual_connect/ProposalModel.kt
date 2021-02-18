package updated_mysterium_vpn.model.manual_connect

import android.graphics.Bitmap
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.QualityLevel
import network.mysterium.proposal.ServiceType
import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.service.core.ProposalPaymentRate
import network.mysterium.ui.Countries
import updated_mysterium_vpn.database.entity.NodeEntity
import java.util.Locale

data class ProposalModel(
        val id: String,
        val providerID: String,
        val serviceType: ServiceType,
        val countryCode: String,
        val nodeType: NodeType,
        val monitoringFailed: Boolean,
        val payment: ProposalPaymentMethod,
        var countryFlagImage: Bitmap?,
        var qualityLevel: QualityLevel,
        var countryName: String
) {

    companion object {

        fun createProposalFromNode(nodeEntity: NodeEntity): ProposalModel {
            val countryCode = nodeEntity.countryCode.toLowerCase(Locale.ROOT)
            val payment = ProposalPaymentMethod(
                    type = nodeEntity.paymentType,
                    price = ProposalPaymentMoney(nodeEntity.paymentAmount, nodeEntity.currency),
                    rate = ProposalPaymentRate(nodeEntity.pricePerSecond, nodeEntity.pricePerByte)
            )
            val countryFlagImage = if (Countries.bitmaps.contains(countryCode)) {
                Countries.bitmaps[countryCode]
            } else {
                null
            }
            return ProposalModel(
                    id = nodeEntity.id,
                    providerID = nodeEntity.providerID,
                    serviceType = ServiceType.parse(nodeEntity.serviceType),
                    countryCode = countryCode,
                    nodeType = NodeType.parse(nodeEntity.nodeType),
                    monitoringFailed = nodeEntity.monitoringFailed,
                    payment = payment,
                    countryFlagImage = countryFlagImage,
                    qualityLevel = QualityLevel.parse(nodeEntity.qualityLevel),
                    countryName = Countries.values[countryCode]?.name ?: ""
            )
        }
    }
}
