package updated.mysterium.vpn.common.data

import network.mysterium.ui.UnitFormatter

object DataUtil {

    private const val ETHER_VALUE = 1_000_000_000_000_000_000.0 // 1 MYST in wei(token)

    fun convertDataToDataType(bytesDouble: Long, dataType: String) = when (dataType) {
        "B" -> bytesDouble.toFloat()
        "KiB" -> (bytesDouble / UnitFormatter.KB).toFloat()
        "MiB" -> (bytesDouble / UnitFormatter.MB).toFloat()
        else -> (bytesDouble / UnitFormatter.GB).toFloat()
    }

    fun convertTokenToMyst(tokens: Long) = tokens / ETHER_VALUE
}
