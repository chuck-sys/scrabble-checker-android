package ca.cheuksblog.scrabblechecker

import java.io.File
import kotlin.math.min

class Trie(
    val root: Node = Node(isEndpoint = false),
) {
    constructor(filename: String): this() {
        File(filename).forEachLine {
            insert(it)
        }
    }

    data class Node (
        var children: HashMap<Char, Node> = hashMapOf(),
        var value: Char = ' ',
        var inBetweenChars: String = "",
        var isEndpoint: Boolean = false,
    )

    fun insert(word: String) {
        word.fold(root) { node, c ->
            (node.children[c] ?: Node(isEndpoint = false, value = c)).also {
                node.children[c] = it
            }
        }.isEndpoint = true
    }

    fun isValid(word: String): Boolean {
        if (word.isEmpty()) {
            return true
        }

        var i = 0
        var node = root
        while (i < word.length) {
            if (word.substring(i, min(i + node.inBetweenChars.length, word.length)) != node.inBetweenChars) {
                return false
            }

            i += node.inBetweenChars.length

            if (i == word.length) {
                return node.isEndpoint
            }

            node = node.children[word[i]] ?: return false
            i += 1
        }

        return node.isEndpoint && node.inBetweenChars.isEmpty()
    }

    fun Node.collapse() {
        children.values.forEach { it.collapse() }

        if (children.size == 1 && !isEndpoint) {
            val (c, child) = children.entries.first()

            inBetweenChars += c + child.inBetweenChars
            isEndpoint = child.isEndpoint
            children = child.children
        }
    }

    fun optimize() {
        root.collapse()
    }
}