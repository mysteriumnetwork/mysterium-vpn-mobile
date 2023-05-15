package network.mysterium.provider.utils

object Converter {
    fun megabytesToBytes(megabytes: Long): Long {
        return megabytes * 1024 * 1024
    }

    fun bytesToMegabytes(bytes: Long): Long {
        return bytes / (1024 * 1024)
    }
}