package network.mysterium.payment

import network.mysterium.vpn.R

data class Currency(val code: String) {
    fun name(): String {
        return when (code) {
            "BTC" -> "Bitcoin"
            "BCH" -> "Bitcoin Cash"
            "DAI" -> "Dai"
            "ETH" -> "Ethereum"
            "LTC" -> "Litecoin"
            "USDT" -> "Tether"
            else -> code
        }
    }

    fun logoResourceId(): Int {
        return when (code) {
            "BTC" -> R.drawable.crypto_btc
            "BCH" -> R.drawable.crypto_bch
            "DAI" -> R.drawable.crypto_dai
            "ETH" -> R.drawable.crypto_eth
            "LTC" -> R.drawable.crypto_ltc
            "USDT" -> R.drawable.crypto_usdt
            else -> R.drawable.ic_help_outline_24dp
        }
    }

    fun supportsLightning(): Boolean {
        return when (code) {
            "BTC", "LTC" -> true
            else -> false
        }
    }

}
