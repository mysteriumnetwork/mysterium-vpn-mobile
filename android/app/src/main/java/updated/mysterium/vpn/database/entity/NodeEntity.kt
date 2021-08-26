package updated.mysterium.vpn.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.nodes.ProposalItem

@Entity
data class NodeEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "provider_id") val providerID: String,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "node_type") val nodeType: String,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "price_per_second") val pricePerSecond: Double,
    @ColumnInfo(name = "price_per_byte") val pricePerByte: Double,
    @ColumnInfo(name = "quality_level") var qualityLevel: Int,
    @ColumnInfo(name = "is_saved") var isSaved: Boolean,
) {

    private companion object {
        const val ETHER_VALUE = 1_000_000_000_000_000_000 // 1e18
    }

    constructor(proposalItem: ProposalItem, isFavourite: Boolean = false) : this(
        id = proposalItem.providerID + proposalItem.serviceType,
        providerID = proposalItem.providerID,
        serviceType = proposalItem.serviceType,
        countryCode = proposalItem.countryCode,
        nodeType = proposalItem.nodeType,
        currency = proposalItem.payment.currency,
        pricePerSecond = proposalItem.payment.perHour / 60 / 60,
        pricePerByte = proposalItem.payment.perGib / 1024 / 1024 / 1024,
        qualityLevel = proposalItem.qualityLevel,
        isSaved = isFavourite
    )

    constructor(proposal: Proposal, isFavourite: Boolean = false) : this(
        id = proposal.providerID + proposal.serviceType,
        providerID = proposal.providerID,
        serviceType = proposal.serviceType.type,
        countryCode = proposal.countryCode,
        nodeType = proposal.nodeType.nodeType,
        currency = proposal.payment.currency,
        pricePerSecond = proposal.payment.perHour / 60 / 60 * ETHER_VALUE,
        pricePerByte = proposal.payment.perGib / 1024 / 1024 / 1024 * ETHER_VALUE,
        qualityLevel = proposal.qualityLevel.level,
        isSaved = isFavourite
    )
}
