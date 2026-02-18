package ca.cheuksblog.scrabblechecker

import java.io.File
import java.io.FileInputStream
import java.nio.channels.FileChannel
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrieSearcherTest {
    @Test
    fun loadAndValidate() {
        val f = File("../app/src/main/res/raw/csw24.bin")
        val stream = FileInputStream(f)
        val buffer = stream.channel.map(FileChannel.MapMode.READ_ONLY, 5, f.length() - 5)
        val searcher = TrieSearcher(buffer)

        assertTrue(searcher.isValid("AA".toByteArray()))
        assertTrue(searcher.isValid("AAL".toByteArray()))
        assertFalse(searcher.isValid("AAT".toByteArray()))
        assertTrue(searcher.isValid("AATMAN".toByteArray()))
        assertTrue(searcher.isValid("AATMANS".toByteArray()))
        assertTrue(searcher.isValid("ZZZ".toByteArray()))
        assertTrue(searcher.isValid("ZZZS".toByteArray()))
    }
}