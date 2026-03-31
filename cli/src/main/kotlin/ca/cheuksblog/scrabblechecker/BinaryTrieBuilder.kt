package ca.cheuksblog.scrabblechecker

import ca.cheuksblog.scrabblechecker.Trie.Node
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import kotlin.math.abs

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
    buffer.put(2)

    // BFS
    val queue: ArrayDeque<Node> = ArrayDeque()
    queue.addLast(trie.root)
    val nodeToOffset = hashMapOf<Int, Int>()
    val unknownChildLocations = arrayListOf<Pair<Int, Int>>()
    var largestOffset = 0
    var n = 0
    var knowns = 0
    while (queue.isNotEmpty()) {
        n += 1
        val node = queue.removeFirst()
        val canBeLeaf = node.children.values.all({ it.isEndpoint && it.inBetweenChars.isEmpty() && it.children.isEmpty() })
        // bit 31 of the mask is for denoting the endpoint
        // bit 27-30 denoting length of in-between bits
        val mask = toChildMask(node.children) or
                (node.inBetweenChars.length.toUInt() shl 27) or
                if (node.isEndpoint) {1u shl 31} else {0u} or
                if (canBeLeaf) {1u shl 26} else {0u}

        nodeToOffset[node.id] = buffer.position()
        buffer.putInt(mask.toInt())                                 // mask 4 bytes
        if (node.inBetweenChars.isNotEmpty()) {                          // characters in between
            buffer.put(node.inBetweenChars.toByteArray())
        }

        if (!canBeLeaf) {
            for ((_, child) in node.children.toList().sortedBy { it.first }) {
                val childLocation = nodeToOffset[child.id]
                if (childLocation == null) {
                    unknownChildLocations.add(Pair(child.id, buffer.position()))
                    buffer.put(byteArrayOf(0, 0, 0))
                } else {
                    buffer.put(intTo3Bytes(childLocation - buffer.position()))
                    knowns++
                }

                queue.addLast(child)            // all sorted children
            }
        }
    }

    println("Unique offsets: ${nodeToOffset.size}")
    println("We hit knowns $knowns times")

    for ((id, offset) in unknownChildLocations) {
        val childLocation = nodeToOffset[id]
        if (childLocation == null) {
            println("Could not get offset of node id = $id")
            break
        }

        buffer.put(offset, intTo3Bytes(childLocation - offset))

        if (abs(childLocation - offset) > largestOffset) {
            largestOffset = abs(childLocation - offset)
        }
    }

    println("Number of nodes: $n")
    println("Largest byte offset: $largestOffset")

    val finalSize = buffer.position()
    buffer.force()

    file.channel.truncate(finalSize.toLong())
    file.close()
}