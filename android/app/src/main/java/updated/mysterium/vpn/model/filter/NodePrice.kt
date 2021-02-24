package updated.mysterium.vpn.model.filter

import updated.mysterium.vpn.model.manual.connect.PriceLevel

enum class NodePrice {
    LOW,
    MEDIUM,
    HIGH;

    companion object {

        fun from(priceLevel: PriceLevel) = when (priceLevel) {
            PriceLevel.HIGH -> HIGH
            PriceLevel.MEDIUM -> MEDIUM
            else -> LOW
        }
    }
}
