package updated.mysterium.vpn.model.manual.connect

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.QualityLevel
import network.mysterium.proposal.ServiceType
import network.mysterium.service.core.ProposalPaymentMethod
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.service.core.ProposalPaymentRate
import network.mysterium.ui.Countries
import updated.mysterium.vpn.database.entity.NodeEntity
import java.util.*

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
): Parcelable {

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
                countryName = Countries.values[countryCode]?.name ?: "Unknown"
            )
        }
    }
}
