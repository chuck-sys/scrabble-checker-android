package ca.cheuksblog.scrabblechecker

import kotlin.math.abs

/**
 * Convert 3-byte signed integer into 4-byte signed integer, which we can work with. We use Big
 * Endian.
 */
fun bytesToInt(x: ByteArray): Int {
    require(x.size == 3) { "Byte array must be exactly 3 bytes long" }
    val raw = ((x[0].toInt() and 0xff) shl 16) or
            ((x[1].toInt() and 0xff) shl 8) or
            (x[2].toInt() and 0xff)
    // Sign extension: if the 24th bit is 1, the number is negative. We shift left by 8 to push the
    // 24th bit to the 32 position, then shift right to fill in the top 8 bits
    return raw.shl(8).shr(8)
}

/**
 * Convert a signed integer (4 bytes) to 3 bytes signed integer. If the integer will overflow the 3
 * bytes, throw an exception. Uses 2's complement.
 */
fun intTo3Bytes(x: Int): ByteArray {
    if (abs(x) > 8388607) {
        throw RuntimeException("Integer ($x) doesn't fit within 3 bytes!")
    }

    return byteArrayOf(
        (x shr 16).toByte(),
        (x shr 8).toByte(),
        x.toByte()
    )
}