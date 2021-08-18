package org.lecomte.codingame.competitive.witch

import java.util.*

const val INVENTORY_MAX_SIZE = 10
const val MAX_TURNS = 100

data class Pos(val tier0: Int, val tier1: Int, val tier2: Int, val tier3: Int) {
    fun add(pos: Pos) = Pos(
        tier0 + pos.tier0,
        tier1 + pos.tier1,
        tier2 + pos.tier2,
        tier3 + pos.tier3
    )

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

val emptyPos = Pos(0, 0, 0, 0)

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

class PathToWitch(val witch: Witch, val path: Path) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PathToWitch) return false

        if (witch != other.witch) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = witch.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
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
        return pos.power() + if (isOnlyPositive()) 10 else 0 + if (repeatable) 1 else 0
    }

    override fun toString() = "S($id)"
}

val rest = Spell(-1, emptyPos, false)
val brew = Spell(-2, emptyPos, false)

class Potion(id: Int, pos: Pos, val price: Int) : Action(id, pos) {
    override fun toString() = "P($id, $price)"
}

data class SpellToLearn(val spell: Spell, val cost: Int, val gain: Int)

class SpellBook(val spells: List<Pair<Spell, Boolean>>) {
    //val spells: MutableList<Pair<Spell, Boolean>> = mutableListOf()

    fun contains(spell: Spell): Boolean = spells.map { it.first.id }.contains(spell.id)

    fun add(spell: Spell, castable: Boolean): SpellBook {
        return if (contains(spell)) {
            this
        } else {
            SpellBook(spells + Pair(spell, castable))
        }
    }

    fun castable(): List<Spell> {
        return spells.filter { it.second }.map { it.first }
    }

    fun isCastable(spell: Spell): Boolean {
        return castable().contains(spell)
    }

    fun cast(spellToCast: Spell): SpellBook {
        return SpellBook(spells.map { if (spellToCast == it.first) Pair(spellToCast, false) else it })
    }

    fun rest(): SpellBook {
        val restedSpells = spells.map { elt -> Pair(elt.first, true) }
        return SpellBook(restedSpells)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpellBook) return false

        if (spells != other.spells) return false

        return true
    }

    override fun hashCode(): Int {
        return spells.hashCode()
    }

    override fun toString(): String {
        return "SpellBook(spells=$spells)"
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
            .maxByOrNull { it.spell.power() + it.gain }?.spell
    }
}

data class Witch(
    var inventory: Inventory,
    var pathToTarget: Pair<Potion, List<Pair<Spell, Int>>>,
    var score: Int,
    var spellBook: SpellBook,
    var brewCount: Int
) {

    fun cast(spell: Spell): Witch {
        return Witch(
            inventory.apply(spell),
            pathToTarget,
            score,
            spellBook.cast(spell),
            brewCount
        )
    }

    fun rest(): Witch {
        return Witch(
            inventory,
            pathToTarget,
            score,
            spellBook.rest(),
            brewCount
        )
    }

    fun castable(): List<Spell> {
        return spellBook.castable().filter { inventory.isAppliable(it) }
    }

    fun hasTarget() = pathToTarget.first != emptyPotion

    fun setTarget(newTarget: Pair<Potion, List<Pair<Spell, Int>>>) {
        pathToTarget = newTarget
    }

    fun advancePath(): Pair<Spell, Int> {
        return if (pathToTarget.second.isEmpty()) {
            Pair(brew, 1)
        } else {
            val first = this.pathToTarget.second[0]
            this.pathToTarget = Pair(this.pathToTarget.first, this.pathToTarget.second.drop(1))
            first
        }
    }
}


val emptyInventory = Inventory(emptyPos)
val emptyPath = listOf<Pair<Spell, Int>>()
val emptyPotion = Potion(0, emptyPos, 0)

val myWitch = Witch(emptyInventory, Pair(emptyPotion, emptyPath), 0, SpellBook(listOf()), 0)
val opponentWitch = Witch(emptyInventory, Pair(emptyPotion, emptyPath), 0, SpellBook(listOf()), 0)

fun main() {
    val input = Scanner(System.`in`)

    var currentTurn = 0

    // game loop
    while (true) {
        currentTurn++
        val potions = mutableListOf<Potion>()
        var mySpellBook = SpellBook(listOf())
        var opponentSpellBook = SpellBook(listOf())
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
                    mySpellBook = mySpellBook.add(Spell(actionId, delta, repeatable), castable)
                }
                "OPPONENT_CAST" -> {
                    opponentSpellBook = opponentSpellBook.add(Spell(actionId, delta, repeatable), castable)
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
            if (score > currentWitch.score) {
                currentWitch.brewCount++
            }
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

private fun price(potions: List<Potion>, potion: Potion): Int {
    val bonus = when (potions.indexOf(potion)) {
        0 -> 3
        1 -> 1
        else -> 0
    }
    return potion.price + bonus
}

private fun computeAction(turn: Int, potions: MutableList<Potion>, tome: Tome, startTime: Long): String {
    if (myWitch.hasTarget() && !potions.contains(myWitch.pathToTarget.first)) {
        System.err.println("Resetting target ${myWitch.pathToTarget.first} with ${myWitch.pathToTarget.second}")
        myWitch.setTarget(Pair(emptyPotion, emptyPath))
    }

    val pathSelectionStrategy = getPathSelectionStrategy()

    if (myWitch.hasTarget()) {
        System.err.println("Targeting ${myWitch.pathToTarget.first} with ${myWitch.pathToTarget.second}")
    } else {
        System.err.println("No target")
    }

    val worthySpell = spellToLearn(myWitch.spellBook, myWitch.inventory, tome)
    if (worthySpell != null) {
        System.err.println("Learn a worthy spell ${worthySpell.id}")
        return "LEARN ${worthySpell.id}"
    }

    val retainedPaths = getRetainedPaths(myWitch, potions, startTime)
    if (retainedPaths.isNotEmpty()) {
        System.err.println("retained size ${retainedPaths.size}")
        retainedPaths.forEach { System.err.println("Potion: ${it.key}, length ${it.value.path}") }

        // Path Selection strategy
        val bestPathToPotion = retainedPaths
            .map { Pair(it.key, it.value.path.compact()) }
            .filter { it.second.size < MAX_TURNS - turn }
            .maxByOrNull { pathSelectionStrategy(potions, it) }

        if (myWitch.hasTarget()) {
            if (pathSelectionStrategy(potions, bestPathToPotion!!) > pathSelectionStrategy(
                    potions,
                    myWitch.pathToTarget
                )
            ) {
                System.err.println("Changing to target ${bestPathToPotion.first} with ${bestPathToPotion.second}")
                myWitch.setTarget(bestPathToPotion)
            }
        } else {
            System.err.println("Setting new target ${bestPathToPotion!!.first} with ${bestPathToPotion.second}")
            myWitch.setTarget(bestPathToPotion)
        }

        return castSpellOrRest(myWitch)
    } else {
        if (myWitch.hasTarget()) {
            System.err.println("Weird: no retained path even though we have a target ${myWitch.pathToTarget.first}")
            return castSpellOrRest(myWitch)
        }
    }

    System.err.println("nothing was retained, ${myWitch.spellBook}")
    val spell = myWitch.castable().sortedByDescending { it.power() }.elementAtOrNull(0)
    return if (spell == null) {
        "REST"
    } else {
        System.err.println("cast some spell ${spell.id}")
        "CAST ${spell.id}"
    }
}

private fun getPathSelectionStrategy(): (List<Potion>, Pair<Potion, List<Pair<Spell, Int>>>) -> Int {
    val maxPriceStrategy = { potions: List<Potion>, target: Pair<Potion, List<Pair<Spell, Int>>> ->
        price(potions, target.first)
    }
    val shortestPathStrategy = { _: List<Potion>, target: Pair<Potion, List<Pair<Spell, Int>>> ->
        -target.second.size
    }
    val balancedStrategy = { potions: List<Potion>, target: Pair<Potion, List<Pair<Spell, Int>>> ->
        if (target.second.isEmpty()) price(potions, target.first) else price(potions, target.first) / target.second.size
    }

    val selectedStrategy: (List<Potion>, Pair<Potion, List<Pair<Spell, Int>>>) -> Int = balancedStrategy
    return selectedStrategy
}

private fun castSpellOrRest(witch: Witch): String {
    val pathElement = witch.advancePath()
    return when (pathElement.first) {
        rest -> {
            "REST"
        }
        brew -> {
            "BREW ${myWitch.pathToTarget.first.id}"
        }
        else -> {
            "CAST ${pathElement.first.id} ${pathElement.second}"
        }
    }
}

private fun spellToLearn(spellBook: SpellBook, inventory: Inventory, tome: Tome): Spell? {
    return when {
        spellBook.spells.size < 9 -> {
            tome.worthiestAffordableSpell(inventory)
        }
        else -> {
            null
        }
    }
}

private fun getRetainedPaths(
    startingWitch: Witch,
    potions: MutableList<Potion>,
    startTime: Long
): MutableMap<Potion, PathToWitch> {
    // Each node of the graph will be a witch state and the path to obtain it
    var alreadyGeneratedPaths = listOf(PathToWitch(startingWitch, Path(listOf())))
    // retainedPaths maps a potion to the first found path to inventory which allow it
    val retainedPaths = mutableMapOf<Potion, PathToWitch>()
    var depth = 0
    var computeTime: Long
    var stop = false
    while (!stop) {
        depth++
        //System.err.println("computed depth $depth, computation time $computeTime")
        val newlyGeneratedPaths = mutableListOf<PathToWitch>()
        // generate new paths from previous ones
        for (currentPath in alreadyGeneratedPaths) {
            val spellBook = currentPath.witch.spellBook
            val inventory = currentPath.witch.inventory
            val affordableSpells = spellBook.spells.map { it.first }.filter { inventory.isAppliable(it) }
            val fromCurrent = affordableSpells.map {
                if (spellBook.isCastable(it))
                    PathToWitch(currentPath.witch.cast(it), currentPath.path.add(it))
                else
                    PathToWitch(currentPath.witch.rest(), currentPath.path.add(rest))
            }
            newlyGeneratedPaths.addAll(fromCurrent)
        }
        //System.err.println("depth $depth, current size: ${pathsToInventory.size}, new size: ${newPathsToInventory.size}, time: ${computeTime(startTime)}")
        // retain those that we don't already have and that allow to brew
        for (retainedPathCandidate in newlyGeneratedPaths) {
            if (retainedPaths.size == potions.size) {
                break
            }
            potions.filter { !retainedPaths.containsKey(it) }
                .filter { retainedPathCandidate.witch.inventory.isAppliable(it) }
                .forEach { retainedPaths[it] = retainedPathCandidate }
        }

        alreadyGeneratedPaths = newlyGeneratedPaths

        computeTime = computeTime(startTime)
        stop = computeTime > 15 || retainedPaths.size == potions.size || newlyGeneratedPaths.size > 900
    }
    System.err.println("depth $depth, final size: ${alreadyGeneratedPaths.size}, time: ${computeTime(startTime)}")
    //System.err.println("end of generation")
    return retainedPaths
}
