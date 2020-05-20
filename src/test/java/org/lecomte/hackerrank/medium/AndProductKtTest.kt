package org.lecomte.hackerrank.medium

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class AndProductKtTest {

    @Test
    fun testAndProduct() {
        val inputStream = Scanner(javaClass.getResourceAsStream("AndProductInput.txt"))
        val expectedStream = Scanner(javaClass.getResourceAsStream("AndProductOutput.txt"))

        val n = inputStream.nextLine().trim().toInt()

        for (nItr in 1..n) {
            val ab = inputStream.nextLine().split(" ")

            val a = ab[0].trim().toLong()
            val b = ab[1].trim().toLong()

            val actual = andProduct(a, b)
            val expected = expectedStream.nextLine().trim().toLong()

            assertThat(actual).withFailMessage("Error for values $a and $b. Expected: $expected, actual: $actual").isEqualTo(expected)
        }
    }
}