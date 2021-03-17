package updated.mysterium.vpn.common.data

import network.mysterium.ui.UnitFormatter

object DataUtil {

    fun convertDataToDataType(bytesDouble: Long, dataType: String) = when (dataType) {
        "B" -> bytesDouble.toFloat()
        "KB" -> (bytesDouble / UnitFormatter.KB).toFloat()
        "MB" -> (bytesDouble / UnitFormatter.MB).toFloat()
        else -> (bytesDouble / UnitFormatter.GB).toFloat()
    }
}
