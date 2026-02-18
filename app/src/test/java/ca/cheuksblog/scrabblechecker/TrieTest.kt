package ca.cheuksblog.scrabblechecker

import org.junit.Test

import org.junit.Assert.*

class TrieTest {
    @Test
    fun empty_trie() {
        val trie = Trie()
        assertFalse(trie.isValid("QUETZAL"))
    }

    @Test
    fun singular_element_in_trie() {
        val trie = Trie()
        trie.insert("QUETZAL")

        assertTrue(trie.isValid("QUETZAL"))
        assertFalse(trie.isValid("quetzal"))
        assertFalse(trie.isValid("QUETZALS"))
        assertFalse(trie.isValid("QUETZA"))
    }
}