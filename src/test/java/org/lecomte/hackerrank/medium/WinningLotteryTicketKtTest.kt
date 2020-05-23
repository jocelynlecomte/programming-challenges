package org.lecomte.hackerrank.medium

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

internal class WinningLotteryTicketKtTest {
    @Test
    fun testWinningLotteryTicketsCustom() {
        testWinningLotteryTickets("WinningLotteryTicketInputCustom.txt", "WinningLotteryTicketOutputCustom.txt")
    }

    @Test
    fun testWinningLotteryTickets01() {
        testWinningLotteryTickets("WinningLotteryTicketInput01.txt", "WinningLotteryTicketOutput01.txt")
    }

    @Test
    fun testWinningLotteryTickets02() {
        testWinningLotteryTickets("WinningLotteryTicketInput02.txt", "WinningLotteryTicketOutput02.txt")
    }

    @Test
    fun testWinningLotteryTickets14() {
        testWinningLotteryTickets("WinningLotteryTicketInput14.txt", "WinningLotteryTicketOutput14.txt")
    }

    private fun testWinningLotteryTickets(inputFileName: String, outputFileName: String) {
        val inputStream = Scanner(javaClass.getResourceAsStream(inputFileName))
        val expectedStream = Scanner(javaClass.getResourceAsStream(outputFileName))

        val n = inputStream.nextLine().trim().toInt()

        val tickets = Array(n) { "" }
        for (i in 0 until n) {
            val ticketsItem = inputStream.nextLine()
            tickets[i] = ticketsItem
        }

        val actual = winningLotteryTicket(tickets)
        val expected = expectedStream.nextLine().trim().toLong()

        Assertions.assertThat(actual).withFailMessage("Expected: $expected, actual: $actual").isEqualTo(expected)
    }
}