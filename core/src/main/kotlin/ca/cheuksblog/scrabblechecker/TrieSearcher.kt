package ca.cheuksblog.scrabblechecker

import java.nio.ByteBuffer

fun countOnesUntil(x: Int, pos: Int): Int {
    var x = x
    var ones = 0
    for (i in 1..pos) {
        if ((x and 1) == 1) {
            ones += 1
        }

        x = x shr 1
    }
    return ones
}

/**
 * Keeps the entire binary file in memory and searches through that instead of using tons of objects.
 *
 * Probably more efficient, but a bit less developer-friendly.
 *
 * Buffer used should not include versioning information.
 */
class TrieSearcher(private val buffer: ByteBuffer) {
    /**
     * Assume word is already uppercased
     */
    fun isValid(word: ByteArray): Boolean {
        if (word.isEmpty()) {
            return true
        }

        buffer.position(0)

        return isValidHelper(word)
    }

    private fun isValidHelper(word: ByteArray, i: Int = 0): Boolean {
        var i = i

        val mask = buffer.getInt().toUInt()
        val childrenMask = mask and 0x3_ff_ff_ffu
        val isEndpoint = (mask and (1u shl 31)) != 0u
        val isLeaf = (mask and (1u shl 26)) != 0u
        val inBetweenCharsLength = (mask shr 27) and 0xfu

        if (inBetweenCharsLength > 0u) {
            val inBetweenBytes = ByteArray(size = inBetweenCharsLength.toInt())
            buffer.get(inBetweenBytes)

            for (b in inBetweenBytes) {
                if (i == word.size) {
                    return false
                }

                if (word[i] != b) {
                    return false
                }

                i += 1
            }
        }

        if (i == word.size) {
            return isEndpoint
        }

        if (((1u shl (word[i] - 0x41)) and childrenMask) == 0u) {
            return false
        }

        if (i == word.size - 1 && isLeaf) {
            return true
        }

        val offset = countOnesUntil(childrenMask.toInt(), (word[i] - 0x41))
        buffer.position(buffer.position() + offset * 4)

        val jumpToOffset = buffer.getInt()
        buffer.position(buffer.position() + jumpToOffset - 4)

        return isValidHelper(word, i + 1)
    }
}
