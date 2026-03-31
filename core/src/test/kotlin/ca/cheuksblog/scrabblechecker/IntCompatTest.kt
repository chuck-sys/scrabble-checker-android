package ca.cheuksblog.scrabblechecker

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class IntCompatTest {
    @Test
    fun functionalInverse() {
        val numbers = intArrayOf(1, 10, 15, 0xf_ff_ff, -1, -200, -3096, -101400)
        for (number in numbers) {
            assertEquals(number, bytesToInt(intTo3Bytes(number)))
        }
    }

    @Test
    fun outOf3ByteRange() {
        assertThrows<RuntimeException> { intTo3Bytes(0xff_ff_ff) }
    }
}