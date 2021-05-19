package updated.mysterium.vpn.model.manual.connect

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.proposal.QualityLevel
import network.mysterium.proposal.ServiceType
import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.ui.Countries
import updated.mysterium.vpn.database.entity.NodeEntity
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

    constructor(nodeEntity: NodeEntity) : this(
        id = nodeEntity.id,
        providerID = nodeEntity.providerID,
        serviceType = ServiceType.parse(nodeEntity.serviceType),
        countryCode = nodeEntity.countryCode.toLowerCase(Locale.ROOT),
        nodeType = NodeType.parse(nodeEntity.nodeType),
        payment = ProposalPaymentMethod(
            currency = nodeEntity.currency,
            perGib = nodeEntity.pricePerByte * 1024 * 1024 * 1024,
            perHour = nodeEntity.pricePerSecond * 60 * 60
        ),
        countryFlagImage = Countries.bitmaps.getOrDefault(
            nodeEntity.countryCode.toLowerCase(Locale.ROOT),
            null
        ),
        qualityLevel = QualityLevel.parse(nodeEntity.qualityLevel),
        countryName = Countries
            .values[nodeEntity.countryCode.toLowerCase(Locale.ROOT)]
            ?.name ?: "Unknown"
    )

    constructor(proposalViewItem: ProposalViewItem) : this(
        id = proposalViewItem.id,
        providerID = proposalViewItem.providerID,
        serviceType = proposalViewItem.serviceType,
        countryCode = proposalViewItem.countryCode.toLowerCase(Locale.ROOT),
        nodeType = proposalViewItem.nodeType,
        payment = proposalViewItem.payment,
        countryFlagImage = proposalViewItem.countryFlagImage,
        qualityLevel = proposalViewItem.qualityLevel,
        countryName = Countries
            .values[proposalViewItem.countryCode.toLowerCase(Locale.ROOT)]
            ?.name ?: "Unknown"
    )

    var priceLevel = PriceLevel.MEDIUM
    var isAvailable = true
}
