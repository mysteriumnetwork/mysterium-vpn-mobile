package network.mysterium.ui

import org.junit.Assert.*
import org.junit.Test

class UnitFormatterTest {

    @Test
    fun testDurationFormatting() {
        var longVal = 46L
        var res = UnitFormatter.timeDisplay(longVal.toDouble())
        assertEquals("00:00:46", res)

        longVal = 61L
        res = UnitFormatter.timeDisplay(longVal.toDouble())
        assertEquals("00:01:01", res)

        longVal = 3663L
        res = UnitFormatter.timeDisplay(longVal.toDouble())
        assertEquals("01:01:03", res)
    }

    @Test
    fun testBytesFormatting() {
        var longVal = 101L
        var res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("101.0 B", "${res.value} ${res.units}")

        longVal = 1028L
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("1.00 KB", "${res.value} ${res.units}")

        longVal = 81068L
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("79.17 KB", "${res.value} ${res.units}")

        longVal = 9381068L
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("8.95 MB", "${res.value} ${res.units}")

        longVal = 1309381068L
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("1.22 GB", "${res.value} ${res.units}")
    }
}
