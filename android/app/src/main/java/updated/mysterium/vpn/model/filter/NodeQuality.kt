package updated.mysterium.vpn.model.filter

import updated.mysterium.vpn.model.proposal.parameters.QualityLevel

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
