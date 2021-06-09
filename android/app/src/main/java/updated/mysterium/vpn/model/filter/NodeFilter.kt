package updated.mysterium.vpn.model.filter

import updated.mysterium.vpn.common.extensions.next

class NodeFilter {

    var typeFilter = NodeType.ALL
        private set

    var priceFilter = NodePrice.HIGH
        private set

    var qualityFilter = NodeQuality.LOW
        private set

    fun onTypeChanged() {
        typeFilter = typeFilter.next()
    }

    fun onPriceChanged() {
        priceFilter = priceFilter.next()
    }

    fun onQualityChanged() {
        qualityFilter = qualityFilter.next()
    }
}
