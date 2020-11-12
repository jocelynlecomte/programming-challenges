package org.lecomte.codingame.competitive.witch

import java.util.*

data class Delta(val tier0: Int, val tier1: Int, val tier2: Int, val tier3: Int) {
}

data class Brew(val id: Int, val delta: Delta, val price: Int) {

}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args: Array<String>) {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {
        val brews = mutableListOf<Brew>()
        val actionCount = input.nextInt() // the number of spells and recipes in play
        System.err.println("action count $actionCount")
        for (i in 0 until actionCount) {
            val actionId = input.nextInt() // the unique ID of this spell or recipe
            val actionType = input.next() // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            val delta0 = input.nextInt() // tier-0 ingredient change
            val delta1 = input.nextInt() // tier-1 ingredient change
            val delta2 = input.nextInt() // tier-2 ingredient change
            val delta3 = input.nextInt() // tier-3 ingredient change
            val delta = Delta(delta0, delta1, delta2, delta3)

            val price = input.nextInt() // the price in rupees if this is a potion
            val brew = Brew(actionId, delta, price)
            brews.add(brew)

            val tomeIndex = input.nextInt() // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            val taxCount = input.nextInt() // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            val castable = input.nextInt() != 0 // in the first league: always 0; later: 1 if this is a castable player spell
            val repeatable = input.nextInt() != 0 // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

            System.err.println("action $actionType-$actionId: $delta0, $delta1 $delta2 $delta3 -> $price")
        }
        val brew = brews.maxBy { it.price }
        System.err.println("best brew $brew")

        for (i in 0 until 2) {
            val inv0 = input.nextInt() // tier-0 ingredients in inventory
            val inv1 = input.nextInt()
            val inv2 = input.nextInt()
            val inv3 = input.nextInt()
            val score = input.nextInt() // amount of rupees

            System.err.println("inventory $inv0 $inv1 $inv2 $inv3 -> $score")
        }

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");


        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        println("BREW ${brew!!.id}")
    }
}