package updated.mysterium.vpn.model.filter

import network.mysterium.proposal.QualityLevel

enum class NodeQuality {
    LOW,
    MEDIUM,
    HIGH;

    companion object {

        fun from(qualityLevel: QualityLevel) = when (qualityLevel) {
            QualityLevel.MEDIUM -> MEDIUM
            QualityLevel.HIGH -> HIGH
            else -> LOW
        }
    }
}
