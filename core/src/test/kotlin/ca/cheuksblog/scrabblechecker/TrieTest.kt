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

        assertTrue(trie.isValid("AAS"))
        assertFalse(trie.isValid("AAT"))
        assertFalse(trie.isValid("AATMA"))
        assertTrue(trie.isValid("AATMAN"))
        assertTrue(trie.isValid("AATMANS"))

        trie.optimize()

        assertEquals("AA", trie.root.inBetweenChars)
        assertEquals(1, trie.root.children['T']!!.children.size)
        assertEquals("MAN", trie.root.children['T']?.inBetweenChars)

        assertTrue(trie.isValid("AAS"))
        assertFalse(trie.isValid("AAT"))
        assertFalse(trie.isValid("AATMA"))
        assertTrue(trie.isValid("AATMAN"))
        assertTrue(trie.isValid("AATMANS"))
    }
}