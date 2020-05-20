package org.lecomte.hackerrank.medium

fun andNaive(a: Long, b: Long) = (a..b).reduce { acc, num -> acc and num }

fun andProduct(a: Long, b: Long): Long {
    val aBinary = a.toString(2)
    val bBinary = b.toString(2)

    val binaryResult = aBinary.zip(bBinary)
            .takeWhile { (aChar, bChar) -> aChar == bChar }
            .map { (aChar, _) -> aChar }
            .joinToString("")
            .padEnd(aBinary.length, '0')

    return binaryResult.toLong(2)
}

fun message(a: Long, b: Long): String {
    return "naive ${andNaive(a, b)}, real: ${andProduct(a, b)}"
}

fun main() {
    println(message(44379234, 46222945))
}
