package ca.cheuksblog.scrabblechecker

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import java.io.InputStream

class Trie(
    val root: Node = Node(isEndpoint = false),
    var entries: Int = 0,
) {
    constructor(stream: InputStream): this() {
        stream.bufferedReader().forEachLine {
            insert(it.substringBefore(' ').toUpperCase(Locale.current))
        }

        Log.d("Trie", "Loaded file with $entries entries")
    }

    data class Node (
        val children: HashMap<Char, Node> = hashMapOf(),
        var isEndpoint: Boolean = false,
    )

    fun insert(word: String) {
        word.fold(root) { node, c ->
            (node.children[c] ?: Node(isEndpoint = false)).also {
                node.children[c] = it
            }
        }.isEndpoint = true

        entries += 1
    }

    fun isValid(word: String): Boolean {
        if (word.isEmpty()) {
            return true
        }

        return word.fold(root) { node, c ->
            node.children[c] ?: return false
        }.isEndpoint
    }
}