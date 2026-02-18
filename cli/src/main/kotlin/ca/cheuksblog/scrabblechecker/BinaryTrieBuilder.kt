package ca.cheuksblog.scrabblechecker

import ca.cheuksblog.scrabblechecker.Trie.Node
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

fun toChildMask(children: HashMap<Char, Node>): UInt {
    return children.keys.fold(0u, { m, c ->
        m or (1u shl (c - 'A'))
    })
}

fun main(args: Array<String>) {
    val input = args[0]
    val output = args[1]

    val trie = Trie(filename = input)

    trie.optimize()

    val file = RandomAccessFile(output, "rw")
    val buffer = file.channel.map(FileChannel.MapMode.READ_WRITE, 0, File(input).length())

    buffer.put("TRIE".toByteArray())
    buffer.put(1)

    // DFS
    val queue: ArrayDeque<Pair<Node, Int?>> = ArrayDeque()
    queue.addLast(Pair(trie.root, null))
    var largestDiff = 0
    var n = 0
    while (queue.isNotEmpty()) {
        n += 1
        val (node, previousPosition) = queue.removeFirst()
        val canBeLeaf = node.children.values.all({ it.isEndpoint })
        // bit 31 of the mask is for denoting the endpoint
        // bit 27-30 denoting length of in-between bits
        val mask = toChildMask(node.children) or
                (node.inBetweenChars.length.toUInt() shl 27) or
                if (node.isEndpoint) {1u shl 31} else {0u} or
                if (canBeLeaf) {1u shl 26} else {0u}

        if (previousPosition != null) {
            buffer.putInt(previousPosition, buffer.position() - previousPosition)

            if (largestDiff < buffer.position() - previousPosition) {
                largestDiff = buffer.position() - previousPosition
            }
        }

        buffer.putInt(mask.toInt())                                 // mask 4 bytes
        if (node.inBetweenChars.isNotEmpty()) {                          // characters in between
            buffer.put(node.inBetweenChars.toByteArray())
        }

        if (!canBeLeaf) {
            for ((c, child) in node.children.toList().sortedBy { it.first }) {
                queue.addLast(Pair(child, buffer.position()))            // all sorted children
                buffer.putInt(0)
            }
        }
    }

    println("Number of nodes: $n")
    println("Largest byte offset: $largestDiff")

    val finalSize = buffer.position()
    buffer.force()

    file.channel.truncate(finalSize.toLong())
    file.close()
}