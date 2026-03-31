package ca.cheuksblog.scrabblechecker

import java.io.File
import kotlin.math.min

class Trie(
    var nextUnusedId: Int = 1,
    val root: Node = Node(id = 0, isEndpoint = false),
) {
    constructor(filename: String): this() {
        File(filename).forEachLine {
            insert(it)
        }
    }

    data class Node (
        var id: Int = -1,
        var children: HashMap<Char, Node> = hashMapOf(),
        var parents: ArrayList<Node> = arrayListOf(),
        var value: Char = ' ',
        var inBetweenChars: String = "",
        var isEndpoint: Boolean = false,
    )

    fun insert(word: String) {
        word.fold(root) { node, c ->
            (node.children[c] ?: Node(id = nextUnusedId++, isEndpoint = false, value = c)).also {
                node.children[c] = it
                it.parents.add(node)
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

    fun Node.countNodes(): UInt {
        return 1u + children.values.sumOf { it.countNodes() }
    }

    fun Node.similarTo(n: Node): Boolean {
        return isEndpoint == n.isEndpoint
                && inBetweenChars == n.inBetweenChars
                && value == n.value
    }

    fun Node.isLeaf(): Boolean {
        return children.values.all({ it.children.isEmpty() })
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

        // DFS for recursively finding and consolidating branches that are the same
        // 1. Start from the leaves
        // 2. If 2 leaves are the same, replace one of them
        val stack: ArrayDeque<Node> = ArrayDeque()
        stack.addLast(root)
        val uniqueNodes: ArrayDeque<Node> = ArrayDeque()
        while (!stack.isEmpty()) {
            val n = stack.removeLast()
            if (!n.isLeaf()) {
                stack.addAll(n.children.values)
                continue
            }

            var foundMatching = false
            for (other in uniqueNodes) {
                if (n.similarTo(other)) {
                    other.parents.addAll(n.parents)

                    for (parent in n.parents) {
                        parent.children[n.value] = other
                    }

                    foundMatching = true
                    break
                }
            }

            if (!foundMatching) {
                uniqueNodes.addLast(n)
            }
        }

//        val dupeNodes = hashSetOf<Int>()
        uniqueNodes.removeIf { it.parents.size == 1 }
//        dupeNodes.addAll(uniqueNodes.map { it.id })

        // 3. Check parents for the same, until we run out of unique nodes
//        while (true) {
//            for (n in uniqueNodes) {
//                for (x in n.parents) {
//                    for (other in n.parents) {
//                        if (x.id == other.id) {
//                            continue
//                        }
//
//                        if (x.similarTo(other)) {
//
//                        }
//                    }
//                }
//            }
//        }
    }
}