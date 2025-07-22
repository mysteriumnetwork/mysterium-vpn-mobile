package updated.mysterium.vpn.model.manual.connect

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import updated.mysterium.vpn.model.proposal.parameters.ProposalViewItem
import updated.mysterium.vpn.common.location.Countries
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.model.nodes.ProposalPaymentMethod
import updated.mysterium.vpn.model.proposal.parameters.NodeType
import updated.mysterium.vpn.model.proposal.parameters.QualityLevel
import updated.mysterium.vpn.model.proposal.parameters.ServiceType
import java.util.*

@Parcelize
data class Proposal(
    val id: String,
    val providerID: String,
    val serviceType: ServiceType,
    val countryCode: String,
    val nodeType: NodeType,
    val payment: ProposalPaymentMethod,
    val countryFlagImage: Bitmap?,
    val qualityLevel: QualityLevel,
    val countryName: String
) : Parcelable {

    private companion object {
        const val ETHER_VALUE = 1_000_000_000_000_000_000 // 1e18
        const val BYTE_IN_GIB = 1024 * 1024 * 1024
        const val SECOND_IN_HOUR = 60 * 60
    }

    constructor(nodeEntity: NodeEntity) : this(
        id = nodeEntity.id,
        providerID = nodeEntity.providerID,
        serviceType = ServiceType.parse(nodeEntity.serviceType),
        countryCode = nodeEntity.countryCode.lowercase(Locale.ROOT),
        nodeType = NodeType.parse(nodeEntity.nodeType),
        payment = ProposalPaymentMethod(
            currency = nodeEntity.currency,
            perGib = nodeEntity.pricePerByte / ETHER_VALUE * BYTE_IN_GIB,
            perHour = nodeEntity.pricePerSecond / ETHER_VALUE * SECOND_IN_HOUR
        ),
        countryFlagImage = Countries.bitmaps.getOrDefault(
            nodeEntity.countryCode.lowercase(Locale.ROOT),
            null
        ),
        qualityLevel = QualityLevel.parse(nodeEntity.qualityLevel),
        countryName = Countries
            .values[nodeEntity.countryCode.lowercase(Locale.ROOT)]
            ?.name ?: "Unknown"
    )

    constructor(proposalViewItem: ProposalViewItem) : this(
        id = proposalViewItem.id,
        providerID = proposalViewItem.providerID,
        serviceType = proposalViewItem.serviceType,
        countryCode = proposalViewItem.countryCode.lowercase(Locale.ROOT),
        nodeType = proposalViewItem.nodeType,
        payment = proposalViewItem.payment,
        countryFlagImage = proposalViewItem.countryFlagImage,
        qualityLevel = proposalViewItem.qualityLevel,
        countryName = Countries
            .values[proposalViewItem.countryCode.lowercase(Locale.ROOT)]
            ?.name ?: "Unknown"
    )

    var priceLevel = PriceLevel.MEDIUM
    var isAvailable = true
}
