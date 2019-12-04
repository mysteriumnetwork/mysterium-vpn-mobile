import network.mysterium.ui.UnitFormatter
import org.junit.Test
import org.junit.Assert.*

class UnitFormatterTest {

    @Test
    fun testDurationFormatting() {
        var longVal: Long = 46
        var res = UnitFormatter.timeDisplay(longVal.toDouble())
        assertEquals("00:00:46", res)

        longVal = 61
        res = UnitFormatter.timeDisplay(longVal.toDouble())
        assertEquals("00:01:01", res)

        longVal = 3663
        res = UnitFormatter.timeDisplay(longVal.toDouble())
        assertEquals("01:01:03", res)
    }

    @Test
    fun testBytesFormatting() {
        var longVal: Long = 101
        var res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("101.0 B", "${res.value} ${res.units}")

        longVal = 1028
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("1.00 KB", "${res.value} ${res.units}")

        longVal = 81068
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("79.17 KB", "${res.value} ${res.units}")

        longVal = 9381068
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("8.95 MB", "${res.value} ${res.units}")

        longVal = 1309381068
        res = UnitFormatter.bytesDisplay(longVal.toDouble())
        assertEquals("1.22 GB", "${res.value} ${res.units}")
    }
}
