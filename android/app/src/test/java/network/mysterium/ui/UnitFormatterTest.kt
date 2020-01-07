package network.mysterium.ui

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class UnitFormatterTimeDisplayTest(private val value: Long, private val expected: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(46L, "00:00:46"),
                    arrayOf(61L, "00:01:01"),
                    arrayOf(3_663L, "01:01:03")
            )
        }
    }

    @Test
    fun testDisplayValueFormatting() {
        val res = UnitFormatter.timeDisplay(value)
        assertEquals(expected, res)
    }
}

@RunWith(Parameterized::class)
class UnitFormatterBytesDisplayTest(private val value: Long, private val expected: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(101L, "101.0 B"),
                    arrayOf(1_028L, "1.00 KB"),
                    arrayOf(81_068L, "79.17 KB"),
                    arrayOf(9_381_068L, "8.95 MB"),
                    arrayOf(1_309_381_068L, "1.22 GB")
            )
        }
    }

    @Test
    fun testDisplayValueFormatting() {
        val res = UnitFormatter.bytesDisplay(value)
        assertEquals(expected, "${res.value} ${res.units}")
    }
}
