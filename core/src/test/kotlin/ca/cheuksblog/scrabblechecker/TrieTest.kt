package ca.cheuksblog.scrabblechecker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrieTest {
    @Test
    fun loadActualWithOptimization() {
        val trie = Trie("../csw24.txt")

        assertTrue(trie.isValid("AAL"))
        assertTrue(trie.isValid("AATMANS"))
        assertFalse(trie.isValid("AAT"))

        trie.optimize()

        assertTrue(trie.isValid("AAL"))
        assertTrue(trie.isValid("AATMANS"))
        assertFalse(trie.isValid("AAT"))
    }

    @Test
    fun optimization() {
        val trie = Trie()
        trie.insert("AATMANS")
        trie.insert("AATMAN")
        trie.insert("AAS")
        trie.insert("CALZONE")
        trie.insert("CALZONES")

        assertTrue(trie.isValid("AAS"))
        assertFalse(trie.isValid("AAT"))
        assertFalse(trie.isValid("AATMA"))
        assertTrue(trie.isValid("AATMAN"))
        assertTrue(trie.isValid("AATMANS"))
        assertTrue(trie.isValid("CALZONE"))
        assertTrue(trie.isValid("CALZONES"))

        trie.optimize()

        assertTrue(trie.isValid("AAS"))
        assertFalse(trie.isValid("AAT"))
        assertFalse(trie.isValid("AATMA"))
        assertTrue(trie.isValid("AATMAN"))
        assertTrue(trie.isValid("AATMANS"))
        assertTrue(trie.isValid("CALZONE"))
        assertTrue(trie.isValid("CALZONES"))
    }
}