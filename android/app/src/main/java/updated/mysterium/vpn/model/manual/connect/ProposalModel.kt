package updated.mysterium.vpn.model.manual.connect

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.proposal.QualityLevel
import network.mysterium.proposal.ServiceType
import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.service.core.ProposalPaymentRate
import network.mysterium.ui.Countries
import updated.mysterium.vpn.database.entity.NodeEntity
import java.util.Locale

@Parcelize
data class ProposalModel(
    val id: String,
    val providerID: String,
    val serviceType: ServiceType,
    val countryCode: String,
    val nodeType: NodeType,
    val monitoringFailed: Boolean,
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
        monitoringFailed = nodeEntity.monitoringFailed,
        payment = ProposalPaymentMethod(
            type = nodeEntity.paymentType,
            price = ProposalPaymentMoney(nodeEntity.paymentAmount, nodeEntity.currency),
            rate = ProposalPaymentRate(nodeEntity.pricePerSecond, nodeEntity.pricePerByte)
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
        monitoringFailed = proposalViewItem.monitoringFailed,
        payment = proposalViewItem.payment,
        countryFlagImage = proposalViewItem.countryFlagImage,
        qualityLevel = proposalViewItem.qualityLevel,
        countryName = Countries
            .values[proposalViewItem.countryCode.toLowerCase(Locale.ROOT)]
            ?.name ?: "Unknown"
    )

    var priceLevel = PriceLevel.MEDIUM
        private set

    fun calculatePriceLevel(
        minPrice: Double,
        firstPriceBorder: Double,
        secondPriceBorder: Double
    ) {
        priceLevel = when (payment.rate.perBytes) {
            0.0 -> {
                PriceLevel.FREE
            }
            in minPrice..firstPriceBorder -> {
                PriceLevel.LOW
            }
            in firstPriceBorder..secondPriceBorder -> {
                PriceLevel.MEDIUM
            }
            else -> {
                PriceLevel.HIGH
            }
        }
    }
}
