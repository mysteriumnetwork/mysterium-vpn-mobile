package updated.mysterium.vpn.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import network.mysterium.service.core.ProposalItem

@Entity
data class NodeEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "provider_id") val providerID: String,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "node_type") val nodeType: String,
    @ColumnInfo(name = "monitoring_failed") val monitoringFailed: Boolean,
    @ColumnInfo(name = "payment_type") val paymentType: String,
    @ColumnInfo(name = "payment_amount") val paymentAmount: Double,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "price_per_second") val pricePerSecond: Double,
    @ColumnInfo(name = "price_per_byte") val pricePerByte: Double,
    @ColumnInfo(name = "quality_level") var qualityLevel: Int,
) {

    constructor(proposalItem: ProposalItem) : this(
        id = proposalItem.providerID + proposalItem.serviceType,
        providerID = proposalItem.providerID,
        serviceType = proposalItem.serviceType,
        countryCode = proposalItem.countryCode,
        nodeType = proposalItem.nodeType,
        monitoringFailed = proposalItem.monitoringFailed,
        paymentType = proposalItem.payment.type,
        paymentAmount = proposalItem.payment.price.amount,
        currency = proposalItem.payment.price.currency,
        pricePerSecond = proposalItem.payment.rate.perSeconds,
        pricePerByte = proposalItem.payment.rate.perBytes,
        qualityLevel = proposalItem.qualityLevel
    )
}
