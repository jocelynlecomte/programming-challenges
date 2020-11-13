package org.lecomte.codingame.competitive.witch

import java.util.*
import kotlin.math.abs

const val INVENTORY_MAX_SIZE = 10

data class Pos(val tier0: Int, val tier1: Int, val tier2: Int, val tier3: Int) {
    fun add(pos: Pos): Pos {
        return Pos(
                tier0 + pos.tier0,
                tier1 + pos.tier1,
                tier2 + pos.tier2,
                tier3 + pos.tier3)
    }

    fun distance(pos: Pos): Int {
        return abs(pos.tier0 - tier0) +
                abs(pos.tier1 - tier1) +
                abs(pos.tier2 - tier2) +
                abs(pos.tier3 - tier3)
    }

    fun sum(): Int {
        return tier0 + tier1 + tier2 + tier3
    }
}

class Inventory(var content: Pos) {
    val size: Int = content.sum()

    fun isAppliable(action: Action): Boolean {
        val pos = this.content.add(action.pos)
        return pos.tier0 >= 0 &&
                pos.tier1 >= 0 &&
                pos.tier2 >= 0 &&
                pos.tier3 >= 0 &&
                pos.sum() <= INVENTORY_MAX_SIZE
    }

    fun apply(action: Action): Inventory {
        val pos = this.content.add(action.pos)
        return Inventory(pos)
    }

    fun balanceDistance(): Int {
        val balanced = Pos(3, 3, 2, 2)
        return this.content.distance(balanced)
    }
}

abstract class Action(val id: Int, val pos: Pos) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Action) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}

class Spell(id: Int, pos: Pos) : Action(id, pos)

class Potion(id: Int, pos: Pos, val price: Int) : Action(id, pos)

class Witch(val inventory: Inventory, val score: Int, val exhaustedSpells: List<Int>)

fun main() {
    val input = Scanner(System.`in`)

    val exhaustedSpells = mutableListOf<Int>()
    var currentTurn = 0

    // game loop
    while (true) {
        currentTurn++
        val witches = mutableListOf<Witch>()
        val potions = mutableListOf<Potion>()
        val spells = mutableListOf<Spell>()

        val actionCount = input.nextInt() // the number of spells and recipes in play
        //System.err.println("action count $actionCount")
        for (i in 0 until actionCount) {
            val actionId = input.nextInt() // the unique ID of this spell or recipe
            val actionType = input.next() // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            val delta0 = input.nextInt() // tier-0 ingredient change
            val delta1 = input.nextInt() // tier-1 ingredient change
            val delta2 = input.nextInt() // tier-2 ingredient change
            val delta3 = input.nextInt() // tier-3 ingredient change
            val delta = Pos(delta0, delta1, delta2, delta3)

            val price = input.nextInt() // the price in rupees if this is a potion
            when (actionType) {
                "BREW" -> {
                    potions.add(Potion(actionId, delta, price))
                }
                "CAST" -> {
                    spells.add(Spell(actionId, delta))
                }
            }

            val tomeIndex = input.nextInt() // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            val taxCount = input.nextInt() // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            val castable = input.nextInt() != 0 // in the first league: always 0; later: 1 if this is a castable player spell
            val repeatable = input.nextInt() != 0 // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

            //System.err.println("action $actionType-$actionId: $delta0, $delta1 $delta2 $delta3 -> $price")
        }

        for (i in 0 until 2) {
            val inv0 = input.nextInt() // tier-0 ingredients in inventory
            val inv1 = input.nextInt()
            val inv2 = input.nextInt()
            val inv3 = input.nextInt()
            val pos = Pos(inv0, inv1, inv2, inv3)
            val inventory = Inventory(pos)

            val score = input.nextInt() // amount of rupees

            val witch = Witch(inventory, score, exhaustedSpells)
            witches.add(witch)
            //System.err.println("inventory $inv0 $inv1 $inv2 $inv3 -> $score")
        }

        val myWitch = witches[0]

        var action: String = "WAIT"
        val brew = potions.filter { myWitch.inventory.isAppliable(it) }.maxBy { it.price }
        if (brew != null) {
            System.err.println("best doable brew $brew")
            action = "BREW ${brew.id}"
        } else if (exhaustedSpells.isNotEmpty()) {
            action = "REST"
            exhaustedSpells.clear()
        } else {
            val spellsToDistance = spells
                    .filterNot { exhaustedSpells.contains(it.id) }
                    .filter { myWitch.inventory.isAppliable(it) }
                    .associateWith { myWitch.inventory.apply(it).balanceDistance() }

            spellsToDistance.forEach { (s: Spell, d: Int) -> System.err.println("Spells ${s.id} produces distance $d") }

            if (spellsToDistance.isNotEmpty()) {
                val minSpellToDistance = spellsToDistance.minBy { it.value }
                val spell = minSpellToDistance!!.key
                action = "CAST ${spell.id}"
                exhaustedSpells.add(spell.id)
            }
        }

        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        println(action)
    }
}