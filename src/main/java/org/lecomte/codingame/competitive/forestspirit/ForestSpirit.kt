package org.lecomte.codingame.competitive.forestspirit

import java.util.*

data class Cell(val index: Int, val richness: Int, val neighbours: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cell) return false

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return index
    }

    override fun toString(): String {
        return "Cell(index=$index, richness=$richness, neighbours=${neighbours.contentToString()})"
    }

}

data class Tree(val cellIndex: Int, val size: Int, val isMine: Boolean, val isDormant: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tree) return false

        if (cellIndex != other.cellIndex) return false

        return true
    }

    override fun hashCode(): Int {
        return cellIndex
    }

    override fun toString(): String {
        return "Tree(cellIndex=$cellIndex, size=$size, isMine=$isMine, isDormant=$isDormant)"
    }
}

const val WAIT = "WAIT"
const val SEED = "SEED"
const val GROW = "GROW"
const val COMPLETE = "COMPLETE"
const val COMPLETE_COST = 4
const val MAX_DAYS = 24

fun parseAction(action: String): Action {
    val parts = action.split(" ")
    return when (parts[0]) {
        WAIT -> Action(WAIT)
        SEED -> Action(SEED, Integer.valueOf(parts[1]), Integer.valueOf(parts[2]))
        else -> Action(parts[0], Integer.valueOf(parts[1]))
    }
}

class Action(val type: String, val sourceCellIdx: Int?, val targetCellIdx: Int?) {

    constructor(type: String) : this(type, null, null)
    constructor(type: String, targetCellIdx: Int) : this(type, null, targetCellIdx)

    override fun toString(): String {
        if (WAIT.equals(type, ignoreCase = true)) {
            return WAIT
        }
        return if (SEED.equals(type, ignoreCase = true)) {
            String.format("%s %d %d", SEED, sourceCellIdx, targetCellIdx)
        } else String.format("%s %d", type, targetCellIdx)
    }
}

class Game {
    var day = 0
    var nutrients = 0
    var board: MutableList<Cell> = ArrayList()
    var possibleActions: MutableList<Action> = ArrayList()
    var trees: MutableList<Tree> = ArrayList()
    var mySun = 0
    var opponentSun = 0
    var myScore = 0
    var opponentScore = 0
    var opponentIsWaiting = false

    fun getNextAction(): Action {
        val seedActions = possibleActionByType(SEED)
        if (seedActions.isNotEmpty() && treeCountBySize(0) == 0) {
            return seedActions.maxBy { board[it.targetCellIdx!!].richness }!!
        }

        val completeActions = possibleActionByType(COMPLETE)
        if (completeActions.size >= bigTreeLimit()) {
            return completeActions.maxBy { actionScore(it) }!!
        }

        val growActions = possibleActionByType(GROW)
        val bestGrowAction = growActions.maxBy { treeByCell(it.targetCellIdx!!)!!.size }
        if (bestGrowAction != null) {
            return bestGrowAction
        }

        return Action("WAIT")
    }

    private fun actionScore(action: Action): Int {
        return when (action.type) {
            COMPLETE -> nutrients + board[action.targetCellIdx!!].richness
            else -> 0
        }
    }

    private fun actionCost(action: Action): Int {
        return when (action.type) {
            COMPLETE -> COMPLETE_COST
            GROW -> {
                val tree = treeByCell(action.targetCellIdx!!)
                return when (tree!!.size) {
                    1 -> 3 + treeCountBySize(2)
                    2 -> 7 + treeCountBySize(3)
                    else -> 0
                }
            }
            SEED -> treeCountBySize(0)
            else -> 0
        }
    }

    private fun possibleActionByType(type: String): List<Action> {
        return possibleActions.filter { it.type == type }
    }

    private fun treeCountBySize(size: Int): Int {
        return trees.filter { it.isMine }.count { it.size == size }
    }

    private fun treeByCell(cellIndex: Int): Tree? {
        return trees.find { it.cellIndex == cellIndex }
    }

    private fun remainingDays() = MAX_DAYS - day

    private fun bigTreeLimit() = remainingDays()
}

fun main() {
    val input = Scanner(System.`in`)
    val game = Game()
    val numberOfCells: Int = input.nextInt()

    for (i in 0 until numberOfCells) {
        val index = input.nextInt() // 0 is the center cell, the next cells spiral outwards
        val richness = input.nextInt() // 0 if the cell is unusable, 1-3 for usable cells
        val neigh0 = input.nextInt() // the index of the neighbouring cell for each direction
        val neigh1 = input.nextInt()
        val neigh2 = input.nextInt()
        val neigh3 = input.nextInt()
        val neigh4 = input.nextInt()
        val neigh5 = input.nextInt()
        val neighs = intArrayOf(neigh0, neigh1, neigh2, neigh3, neigh4, neigh5)
        val cell = Cell(index, richness, neighs)
        game.board.add(cell)
    }

    while (true) {
        game.day = input.nextInt()
        game.nutrients = input.nextInt()
        game.mySun = input.nextInt()
        game.myScore = input.nextInt()
        game.opponentSun = input.nextInt()
        game.opponentScore = input.nextInt()
        game.opponentIsWaiting = input.nextInt() != 0

        game.trees.clear()
        val numberOfTrees: Int = input.nextInt()
        for (i in 0 until numberOfTrees) {
            val cellIndex: Int = input.nextInt()
            val size: Int = input.nextInt()
            val isMine = input.nextInt() != 0
            val isDormant = input.nextInt() != 0
            val tree = Tree(cellIndex, size, isMine, isDormant)
            game.trees.add(tree)
        }

        game.possibleActions.clear()
        val numberOfPossibleActions: Int = input.nextInt()
        input.nextLine()
        for (i in 0 until numberOfPossibleActions) {
            val possibleAction: String = input.nextLine()
            game.possibleActions.add(parseAction(possibleAction))
        }

        System.err.println("possible actions: ${game.possibleActions}")
        System.err.println("nutrients ${game.nutrients}, mySun: ${game.mySun}")
        val action = game.getNextAction()
        println(action)
    }
}
