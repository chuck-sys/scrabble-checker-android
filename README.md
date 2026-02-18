# Scrabble Checker

Newly updated for the 21st century!

- CSW24 dictionary
- Tournament mode (indicate correct/incorrect for all words)
- Non-tournament mode (show the correct/incorrect words)
- Lightweight (uses Trie data structure)
- Uses Jetpack Compose instead of the Android 4 stuff

# Binary format

All integers are stored in big-endian byte order, as per the default for a Java `MappedByteBuffer`.

## Version header

This isn't checked in the app.

Bytes | Number of Bytes | Description
---|---|---
0..=3 | 4 | The magic string `TRIE`
4 | 1 | The version number

## Node

Offset | Number of Bytes | Description
---|---|---
0..=3 | 4 | 32-bit integer representing a mask
4..=(4+n) | n | In-between characters
(4+n)..=(4+n+4k) | 4k | k 32-bit integers of children offsets

### Mask

We count the bit positions starting from 1 up to and including 32.

```kotlin
fun toChildMask(children: HashMap<Char, Node>): UInt {
    return children.keys.fold(0u, { m, c ->
        m or (1u shl (c - 'A'))
    })
}
```

Bit position | Description
---|---
32 | 1 if the node is an endpoint, 0 otherwise
28..=31 | 4-bit length of the number of in-between characters
27 | 1 if the node is a leaf node, 0 otherwise
1..=26 | Alphabetic mask; position 1 is 1 if the node has child `A`, etc.

### In-between characters

An array of up to 15 characters with 1 byte per character. They should be ASCII.

> [!NOTE]
> Since we are dealing with uppercased characters only (don't even **think** about non-English
> dictionaries), we can compress this into 5 bits per character (the 4-bit length would be the number of
> bytes instead of the number of characters), which would allow us to store 24 in-between characters
> instead. This is irrelevant to Scrabble, of course, because the board only allows for 15 characters.

### Children offsets

A list of 32-bit relative offsets that go to the child node. You can get to a specific one by counting
the ones in the alphabetic mask until you reach the character using the following function:

```kotlin
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
```
