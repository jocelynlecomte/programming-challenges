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

    override fun toString(): String {
        return "Pos($tier0, $tier1, $tier2, $tier3)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pos) return false

        if (tier0 != other.tier0) return false
        if (tier1 != other.tier1) return false
        if (tier2 != other.tier2) return false
        if (tier3 != other.tier3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tier0
        result = 31 * result + tier1
        result = 31 * result + tier2
        result = 31 * result + tier3
        return result
    }


}

class Inventory(var content: Pos) {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Inventory) return false

        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun toString(): String {
        return "I($content)"
    }
}

class Path(val path: List<Spell>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Path) return false

        if (path.size != other.path.size) return false

        for (i in path.indices) {
            if (path[i] != other.path[i]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return path.sumBy { it.id }.hashCode()
    }

    override fun toString(): String {
        val value = path.map { it.id }.joinToString(",", "[", "]")
        return "Path$value"
    }
}

class PathToInventory(val inventory: Inventory, val path: List<Spell>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PathToInventory) return false

        if (inventory != other.inventory || path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return inventory.hashCode() + path.hashCode()
    }
}

//typealias InventoryWithPath = Pair<Inventory, List<Spell>>

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

class Spell(id: Int, pos: Pos) : Action(id, pos) {
    override fun toString(): String {
        return "S(${id})"
    }

    fun isTop(): Boolean {
        return pos.tier0 >= 0 && pos.tier1 >= 0 && pos.tier2 >= 0 && pos.tier3 >= 0
    }
}

class Potion(id: Int, pos: Pos, val price: Int) : Action(id, pos) {
    override fun toString(): String {
        return "P(${id})"
    }
}

data class SpellToLearn(val spell: Spell, val cost: Int, val taxCount: Int, val repeatable: Boolean)

class SpellBook {
    val spells: MutableList<Pair<Spell, Boolean>> = mutableListOf()

    fun contains(spell: Spell): Boolean {
        return spells.map { it.first.id }.contains(spell.id)
    }

    fun add(spell: Spell, castable: Boolean) {
        if (!contains(spell)) {
            spells.add(Pair(spell, castable))
        }
    }

    fun castable(): List<Spell> {
        return spells.filter { it.second }.map { it.first }
    }

    fun isCastable(spell: Spell): Boolean {
        return castable().contains(spell)
    }
}

class Tome {
    val spells = mutableListOf<SpellToLearn>()

    fun contains(spell: Spell): Boolean {
        return spells.map { it.spell.id }.contains(spell.id)
    }

    fun add(spell: Spell, cost: Int, gain: Int, repeatable: Boolean) {
        if (!contains(spell)) {
            spells.add(SpellToLearn(spell, cost, gain, repeatable))
        }
    }

    fun worthySpell(maxCost: Int): Spell? {
        return spells.find { it.cost <= maxCost && it.spell.isTop() }?.spell
    }
}

class Witch(val inventory: Inventory, val score: Int, val spellBook: SpellBook)

fun main() {
    val input = Scanner(System.`in`)

    var currentTurn = 0

    // game loop
    while (true) {
        currentTurn++
        val witches = mutableListOf<Witch>()
        val potions = mutableListOf<Potion>()
        val mySpellBook = SpellBook()
        val opponentSpellBook = SpellBook()
        val tome = Tome()

        var startTime: Long = 0
        val actionCount = input.nextInt() // the number of spells and recipes in play
        for (i in 0 until actionCount) {
            val actionId = input.nextInt() // the unique ID of this spell or recipe
            startTime = System.nanoTime()
            val actionType = input.next() // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            val delta0 = input.nextInt() // tier-0 ingredient change
            val delta1 = input.nextInt() // tier-1 ingredient change
            val delta2 = input.nextInt() // tier-2 ingredient change
            val delta3 = input.nextInt() // tier-3 ingredient change
            val delta = Pos(delta0, delta1, delta2, delta3)
            val price = input.nextInt() // the price in rupees if this is a potion
            val tomeIndex = input.nextInt() // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            val taxCount = input.nextInt() // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            val castable = input.nextInt() != 0 // in the first league: always 0; later: 1 if this is a castable player spell
            val repeatable = input.nextInt() != 0 // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

            when (actionType) {
                "BREW" -> {
                    potions.add(Potion(actionId, delta, price))
                }
                "CAST" -> {
                    mySpellBook.add(Spell(actionId, delta), castable)
                }
                "OPPONENT_CAST" -> {
                    opponentSpellBook.add(Spell(actionId, delta), castable)
                }
                "LEARN" -> {
                    tome.add(Spell(actionId, delta), tomeIndex, taxCount, repeatable)
                }
            }


            //System.err.println("action $actionType-$actionId: $price, $tomeIndex, $taxCount, $castable, $repeatable")
        }

        for (i in 0 until 2) {
            val inv0 = input.nextInt() // tier-0 ingredients in inventory
            val inv1 = input.nextInt()
            val inv2 = input.nextInt()
            val inv3 = input.nextInt()
            val pos = Pos(inv0, inv1, inv2, inv3)
            val inventory = Inventory(pos)

            val score = input.nextInt() // amount of rupees

            val witch = Witch(inventory, score, mySpellBook)
            witches.add(witch)
            //System.err.println("inventory $inv0 $inv1 $inv2 $inv3 -> $score")
        }

        val myWitch = witches[0]

        var action: String
        val brew = potions.filter { myWitch.inventory.isAppliable(it) }.maxBy { it.price }
        if (brew != null) {
            System.err.println("best doable brew $brew")
            action = "BREW ${brew.id}"
        } else {
            action = computeAction(myWitch.inventory, mySpellBook, potions, tome, startTime)

        }

        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        System.err.println("Turn $currentTurn computation time : ${computeTime(startTime)}")

        println(action)
    }
}

private fun computeTime(startTime: Long): Long {
    val endTime = System.nanoTime()
    return (endTime - startTime) / 1_000_000
}

private fun computeAction(startingInventory: Inventory, mySpellBook: SpellBook, potions: MutableList<Potion>, tome: Tome, startTime: Long): String {
    val worthySpell = tome.worthySpell(startingInventory.content.tier0)
    if (worthySpell != null) {
        System.err.println("Learn a worthy spell ${worthySpell.id}")
        return "LEARN ${worthySpell.id}"
    }

    // Each node of the graph will be an inventory and the path to obtain it
    var pathsToInventory = listOf(PathToInventory(startingInventory, listOf()))
    // retainedPaths maps a potion to the first found path to inventory which allow it
    val retainedPaths = mutableMapOf<Potion, PathToInventory>()
    var depth = 0
    var computeTime = computeTime(startTime)
    var stop = false
    while (!stop) {
        depth++
        //System.err.println("computed depth $depth, computation time $computeTime")
        val newPathsToInventory = mutableListOf<PathToInventory>()
        // generate new inventories from previous ones
        for (currentPath in pathsToInventory) {
            val appliableSpells = mySpellBook.spells.map { it.first }
                    .filter { currentPath.inventory.isAppliable(it) }
            val fromCurrent = appliableSpells.map { PathToInventory(currentPath.inventory.apply(it), currentPath.path + it) }
            newPathsToInventory.addAll(fromCurrent)
        }
        System.err.println("depth $depth, current size: ${pathsToInventory.size}, new size: ${newPathsToInventory.size}, time: ${computeTime(startTime)}")
        // retain those that we don't already have and that allow to brew
        for (pathToInventory in newPathsToInventory) {
            if (retainedPaths.size == potions.size) {
                break
            }
            potions.filter { !retainedPaths.containsKey(it) }
                    .filter { pathToInventory.inventory.isAppliable(it) }
                    .forEach { retainedPaths[it] = pathToInventory }
        }

        pathsToInventory = newPathsToInventory

        computeTime = computeTime(startTime)
        stop = computeTime > 15 || retainedPaths.size == potions.size
    }
    System.err.println("end of generation")

    //retained.forEach { System.err.println("retained potion $it ") }
    // Check all retained to find the best
    if (retainedPaths.isEmpty()) {
        System.err.println("nothing was retained")
        if (mySpellBook.castable().isEmpty()) {
            return "REST"
        } else {
            val spell = mySpellBook.castable()[0]
            System.err.println("cast some spell ${spell.id}")
            return "CAST ${spell.id}"
        }

    } else {
        System.err.println("retained size ${retainedPaths.size}")
        retainedPaths.forEach { System.err.println("Potion: ${it.key}, length ${it.value.path}") }
        val retainedEntry = retainedPaths.maxBy { entry -> entry.key.price }
        //val retainedEntry = retainedPaths.minBy { entry -> entry.value.path.size }
        val minPath = retainedEntry!!.value
        val spellToCast = minPath.path[0]
        System.err.println("Will try to brew potion ${retainedEntry.key.id}, and cast ${spellToCast.id}")
        return if (mySpellBook.isCastable(spellToCast)) {
            "CAST ${spellToCast.id}"
        } else {
            "REST"
        }
    }
}