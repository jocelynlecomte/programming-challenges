package org.lecomte.codingame.competitive.witch

import java.util.*

const val INVENTORY_MAX_SIZE = 10

data class Pos(val tier0: Int, val tier1: Int, val tier2: Int, val tier3: Int) {
    fun add(pos: Pos) = Pos(
            tier0 + pos.tier0,
            tier1 + pos.tier1,
            tier2 + pos.tier2,
            tier3 + pos.tier3)

    fun sum() = tier0 + tier1 + tier2 + tier3

    fun power() = tier0 + 2 * tier1 + 3 * tier2 + 4 * tier3

    override fun toString() = "Pos($tier0, $tier1, $tier2, $tier3)"

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

    override fun hashCode() = content.hashCode()

    override fun toString() = "I($content)"
}

class Path(val path: List<Spell>) {
    fun first() = path[0]

    fun queue() = Path(path.drop(1))

    fun add(spell: Spell) = Path(path + spell)

    // ABBABABB -> A1 B2 A1 B1 A1 B2
    fun compact(): List<Pair<Spell, Int>> {
        val result: MutableList<Pair<Spell, Int>> = mutableListOf()
        var i = 0
        while (i < path.size) {
            val currentSpell = path[i]
            if (currentSpell.repeatable) {
                var count = 1
                while (i + count < path.size && path[i + count] == currentSpell) {
                    count++
                }
                i += count
                result.add(Pair(currentSpell, count))
            } else {
                i++
                result.add(Pair(currentSpell, 1))
            }
        }
        return result
    }

    fun count(spell: Spell) = path.count { it == spell }

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

    override fun hashCode() = path.sumBy { it.id }

    override fun toString(): String {
        val value = path.map { it.id }.joinToString(",", "[", "]")
        return "Path$value"
    }
}

class PathToInventory(val inventory: Inventory, val path: Path) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PathToInventory) return false

        if (inventory != other.inventory || path != other.path) return false

        return true
    }

    override fun hashCode() = inventory.hashCode() + path.hashCode()
}

abstract class Action(val id: Int, val pos: Pos) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Action) return false

        if (id != other.id) return false
        if (pos != other.pos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + pos.hashCode()
        return result
    }
}

class Spell(id: Int, pos: Pos, val repeatable: Boolean) : Action(id, pos) {
    fun isOnlyPositive(): Boolean {
        return pos.tier0 >= 0 && pos.tier1 >= 0 && pos.tier2 >= 0 && pos.tier3 >= 0
    }

    fun power(): Int {
        return pos.power() + if (isOnlyPositive()) 5 else 0 + if (repeatable) 5 else 0
    }

    override fun toString() = "S($id)"
}

class Potion(id: Int, pos: Pos, val price: Int) : Action(id, pos) {
    override fun toString() = "P($id, $price)"
}

data class SpellToLearn(val spell: Spell, val cost: Int, val gain: Int)

class SpellBook {
    val spells: MutableList<Pair<Spell, Boolean>> = mutableListOf()

    fun contains(spell: Spell): Boolean = spells.map { it.first.id }.contains(spell.id)

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

    fun add(spell: Spell, cost: Int, gain: Int) {
        if (!contains(spell)) {
            spells.add(SpellToLearn(spell, cost, gain))
        }
    }

    fun worthiestAffordableSpell(inventory: Inventory): Spell? {
        return spells
                .filter { it.cost <= inventory.content.tier0 }
                .maxBy { it.spell.power() }?.spell
    }
}

data class Witch(
        var inventory: Inventory,
        var pathToTarget: List<Pair<Spell, Int>>,
        var target: Potion,
        var score: Int,
        var spellBook: SpellBook)

val emptyPos = Pos(0, 0, 0, 0)
val emptyInventory = Inventory(emptyPos)
val emptyPath = listOf<Pair<Spell, Int>>()
val emptyPotion = Potion(0, emptyPos, 0)

val myWitch = Witch(emptyInventory, emptyPath, emptyPotion, 0, SpellBook())
val opponentWitch = Witch(emptyInventory, emptyPath, emptyPotion, 0, SpellBook())

fun main() {
    val input = Scanner(System.`in`)

    var currentTurn = 0

    // game loop
    while (true) {
        currentTurn++
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
                    mySpellBook.add(Spell(actionId, delta, repeatable), castable)
                }
                "OPPONENT_CAST" -> {
                    opponentSpellBook.add(Spell(actionId, delta, repeatable), castable)
                }
                "LEARN" -> {
                    tome.add(Spell(actionId, delta, repeatable), tomeIndex, taxCount)
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

            val currentWitch = if (i == 0) myWitch else opponentWitch
            currentWitch.inventory = inventory
            currentWitch.score = score
            //System.err.println("inventory $inv0 $inv1 $inv2 $inv3 -> $score")
        }

        myWitch.spellBook = mySpellBook
        opponentWitch.spellBook = opponentSpellBook

        val action = computeAction(currentTurn, potions, tome, startTime)

        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        System.err.println("Turn $currentTurn computation time : ${computeTime(startTime)}")

        println(action)
    }
}

private fun computeTime(startTime: Long): Long {
    val endTime = System.nanoTime()
    return (endTime - startTime) / 1_000_000
}

private fun price(potions: MutableList<Potion>, potion: Potion): Int {
    val bonus = when (potions.indexOf(potion)) {
        0 -> 3
        1 -> 1
        else -> 0
    }
    return potion.price + bonus
}

private fun computeAction(turn: Int, potions: MutableList<Potion>, tome: Tome, startTime: Long): String {
    if (potions.contains(myWitch.target)) {
        if (myWitch.pathToTarget.isNotEmpty()) {
            val spell = myWitch.pathToTarget[0]
            return castSpellOrRest(spell.first, spell.second)
        } else {
            System.err.println("ERROR: target ${myWitch.target}")
        }
    }

    myWitch.target = emptyPotion
    myWitch.pathToTarget = emptyPath

    val brew = potions.filter { myWitch.inventory.isAppliable(it) }.maxBy { price(potions, it) }
    if (brew != null && price(potions, brew) > 10) {
        System.err.println("best doable brew $brew")
        return "BREW ${brew.id}"
    }

    val worthySpell = spellToLearn(myWitch.spellBook, myWitch.inventory, tome)
    if (worthySpell != null) {
        System.err.println("Learn a worthy spell ${worthySpell.id}")
        return "LEARN ${worthySpell.id}"
    }

    val retainedPaths = getRetainedPaths(myWitch.inventory, startTime, myWitch.spellBook, potions)
    if (retainedPaths.isNotEmpty()) {
        System.err.println("retained size ${retainedPaths.size}")
        retainedPaths.forEach { System.err.println("Potion: ${it.key}, length ${it.value.path}") }
        val retainedEntry = retainedPaths.maxBy { entry -> price(potions, entry.key) }
        //val retainedEntry = retainedPaths.minBy { entry -> entry.value.path.size }
        // Optimize retained entry
        val optimizedPath = optimizePath(retainedEntry!!.value.path)
        val spellToCast = optimizedPath[0]

        myWitch.target = retainedEntry.key
        myWitch.pathToTarget = optimizedPath
        return castSpellOrRest(spellToCast.first, spellToCast.second)
    }

    System.err.println("nothing was retained")
    val spell = myWitch.spellBook.castable().sortedByDescending { it.power() }.elementAtOrNull(0)
    return if (spell == null) {
        "REST"
    } else {
        System.err.println("cast some spell ${spell.id}")
        "CAST ${spell.id}"
    }
}

fun optimizePath(path: Path): List<Pair<Spell, Int>> {
    return path.compact()
}

private fun castSpellOrRest(spellToCast: Spell, repetitions: Int): String {
    return if (myWitch.spellBook.isCastable(spellToCast)) {
        System.err.println("Going to potion ${myWitch.target}, and CAST ${spellToCast.id} $repetitions times")
        myWitch.pathToTarget = myWitch.pathToTarget.drop(1)
        "CAST ${spellToCast.id} $repetitions"
    } else {
        System.err.println("Going to potion ${myWitch.target}, and would cast ${spellToCast.id} $repetitions times, but must REST")
        "REST"
    }
}

private fun spellToLearn(spellBook: SpellBook, inventory: Inventory, tome: Tome): Spell? {
    return when {
        spellBook.spells.size < 10 -> {
            tome.worthiestAffordableSpell(inventory)
        }
        else -> {
            null
        }
    }
}

private fun getRetainedPaths(startingInventory: Inventory, startTime: Long, mySpellBook: SpellBook, potions: MutableList<Potion>): MutableMap<Potion, PathToInventory> {
    // Each node of the graph will be an inventory and the path to obtain it
    var pathsToInventory = listOf(PathToInventory(startingInventory, Path(listOf())))
    // retainedPaths maps a potion to the first found path to inventory which allow it
    val retainedPaths = mutableMapOf<Potion, PathToInventory>()
    var depth = 0
    var computeTime: Long
    var stop = false
    while (!stop) {
        depth++
        //System.err.println("computed depth $depth, computation time $computeTime")
        val newPathsToInventory = mutableListOf<PathToInventory>()
        // generate new inventories from previous ones
        for (currentPath in pathsToInventory) {
            val appliableSpells = mySpellBook.spells.map { it.first }.filter { currentPath.inventory.isAppliable(it) }
            val fromCurrent = appliableSpells.map { PathToInventory(currentPath.inventory.apply(it), currentPath.path.add(it)) }
            newPathsToInventory.addAll(fromCurrent)
        }
        //System.err.println("depth $depth, current size: ${pathsToInventory.size}, new size: ${newPathsToInventory.size}, time: ${computeTime(startTime)}")
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
        stop = computeTime > 15 || retainedPaths.size == potions.size || newPathsToInventory.size > 5000
    }
    System.err.println("depth $depth, final size: ${pathsToInventory.size}, time: ${computeTime(startTime)}")
    //System.err.println("end of generation")
    return retainedPaths
}