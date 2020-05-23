package org.lecomte.hackerrank.medium

val possibleDigits = "0123456789".toSet()

fun signature(ticket: String) = ticket.toSet()

fun missingDigits(ticketSignature: Set<Char>) = possibleDigits - ticketSignature

fun twoElementsCombinationsCount(n: Long): Long = n * (n - 1)

fun winningLotteryTicket(tickets: Array<String>): Long {
    var result = 0L
    val ticketsBySignature = tickets.groupBy(::signature) { it }

    // handle special case of complete tickets
    val completeTickets = ticketsBySignature.getOrDefault(possibleDigits, emptyList())
    // count the number of pairs of complete tickets
    result += twoElementsCombinationsCount(completeTickets.size.toLong())
    // add the the pairs of complete tickets with incomplete tickets
    result += completeTickets.size * (tickets.size - completeTickets.size)

    // Now only deal with incomplete tickets
    val incompleteTicketsBySignature = ticketsBySignature.filterKeys { it != possibleDigits }
    incompleteTicketsBySignature.keys
            .forEach { signature ->
                val missingDigits = missingDigits(signature)
                val ticketsToMatch = ticketsBySignature.getValue(signature)
                val matchingTickets = ticketsBySignature.filterKeys { it.containsAll(missingDigits) }.values.flatten()

                result += ticketsToMatch.size * matchingTickets.size
            }

    return result / 2
}
