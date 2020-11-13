package org.lecomte.codingame.competitive.pac

import java.time.Instant
import java.util.*

const val WALL_CHAR = '#'
const val EMPTY_CHAR = ' '
const val SIMPLE_PELLET_VALUE = 1
const val SUPER_PELLET_VALUE = 10
const val SAFETY_DISTANCE = 2

fun log(message: Any?) = System.err.print(message)

fun logln(message: Any?) = System.err.println(message)

fun <T, U, V> cartesianProduct(c1: Collection<T>, c2: Collection<U>, combiner: (T, U) -> V): Set<V> {
    return c1.flatMap { x ->
        c2.map { y ->
            combiner(x, y)
        }
    }.toSet()
}

enum class CellContent {
    WALL, FRIENDLY_PAC, ENEMY_PAC, PELLET, SUPER_PELLET, EMPTY
}

enum class Direction {
    NORTH {
        override fun reverse() = SOUTH
    },
    EAST {
        override fun reverse() = WEST
    },
    SOUTH {
        override fun reverse() = NORTH
    },
    WEST {
        override fun reverse() = EAST
    };

    abstract fun reverse(): Direction
}

enum class Strategy(val command: String) {
    MOVE("MOVE"), SPEED("SPEED"), SWITCH("SWITCH"), RUNAWAY("MOVE"), COLLISION("MOVE")
}

enum class PacType {
    ROCK {
        override fun loseTo() = PAPER
    },
    PAPER {
        override fun loseTo() = SCISSORS
    },
    SCISSORS {
        override fun loseTo() = ROCK
    };

    abstract fun loseTo(): PacType
}

data class Pos(val x: Int, val y: Int) {
    fun move(direction: Direction): Pos {
        return when (direction) {
            Direction.NORTH -> Pos(x, y - 1)
            Direction.SOUTH -> Pos(x, y + 1)
            Direction.EAST -> Pos(x + 1, y)
            Direction.WEST -> Pos(x - 1, y)
        }
    }

    /**
     * computes the direction this point should go to move to the other point
     */
    fun directionToMeet(other: Pos): Direction = when {
        x > other.x -> Direction.WEST
        x < other.x -> Direction.EAST
        y > other.y -> Direction.NORTH
        else -> Direction.SOUTH
    }
}

data class Pellet(val pos: Pos, val value: Int) {
    val isSuper = value > 1
}

data class Pac(val id: Int, val mine: Boolean, val pos: Pos, val previousPos: Pos, val type: PacType,
               val abilityCooldown: Int, val previousTarget: Pos) {

    var target: Pos = Pos(0, 0)
    var linesOfSight = mapOf<Direction, List<Pos>>()

    fun canUseAbility(): Boolean = abilityCooldown <= 0

    fun hasCollided() = pos == previousPos

    fun hasTargetChanged() = target != previousTarget

    fun direction(): Direction = previousPos.directionToMeet(pos)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pac

        if (id != other.id) return false
        if (mine != other.mine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + mine.hashCode()
        return result
    }

    override fun toString(): String {
        return "Pac(id=$id, mine=$mine, position=$pos)"
    }
}

class Game(private val width: Int, private val height: Int, private val grid: Array<String>) {
    private val middleRow = width / 2
    private val walkablePositions = grid
            .mapIndexed { rowIndex, row ->
                row
                        .mapIndexed { colIndex, char ->
                            if (char == EMPTY_CHAR) Pos(colIndex, rowIndex) else null
                        }
                        .filterNotNull()
            }
            .flatten().toSet()

    private val possiblePelletPositions = walkablePositions.toMutableSet()

    private val shortestPaths = mutableMapOf<Pair<Pos, Pos>, List<Pos>>()

    private val distance: (Pos, Pos) -> Int = { start, end -> computeShortestPath(start, end).size }
    private val isForbidden: (Pos) -> Boolean = { pos -> pos.x < 0 || pos.x > width - 1 || pos.y < 0 || pos.y > height - 1 || WALL_CHAR == grid[pos.y][pos.x] }
    private val isEnemy: (Pos) -> Boolean = { pos -> cellType(pos) == CellContent.ENEMY_PAC }
    private val symetric: (Pos) -> Pos = { pos -> Pos(2 * middleRow - pos.x, pos.y) }

    init {
        val walkableInLeftHalf = walkablePositions.filter { (x, _) -> x <= middleRow }
        val walkableInRightHalf = walkablePositions.filter { (x, _) -> x >= middleRow }
        val walkablePairsInLeftHalf = cartesianProduct(walkableInLeftHalf, walkableInLeftHalf) { pos1, pos2 ->
            Pair(pos1, pos2)
        }
                .filterNot { (pos1, pos2) -> pos1 == pos2 }

        val walkablePairsInEachHalf = cartesianProduct(walkableInLeftHalf, walkableInRightHalf) { pos1, pos2 ->
            Pair(pos1, pos2)
        }

        logln("init walkable pairs ${walkablePairsInLeftHalf.size}")
        val start = Instant.now()

        walkablePairsInLeftHalf.forEach { (start, end) ->
            val normalSearch = Pair(start, end)
            val reversedsearch = Pair(end, start)
            if (!shortestPaths.contains(normalSearch) && !shortestPaths.contains(reversedsearch)) {
                val symetricSearch = Pair(symetric(start), symetric(end))
                val path = computeShortestPath(start, end)
                val symetricPath = path.map { symetric(it) }
                shortestPaths[normalSearch] = path
                shortestPaths[symetricSearch] = symetricPath
            }
        }

        /*walkablePairsInEachHalf.forEach { (start, end) ->
            val normalSearch = Pair(start, end)
            val reversedsearch = Pair(end, start)
            if (!shortestPaths.contains(normalSearch) && !shortestPaths.contains(reversedsearch)) {
                val path = computeShortestPath(start, end)
                shortestPaths[normalSearch] = path
            }
        }*/

        val end = Instant.now()
        logln("init, it took ${(end.toEpochMilli() - start.toEpochMilli())}")
        logln("init, shortest paths ${shortestPaths.size}")
    }

    var score = 0
    var opponentScore = 0

    var pacs = setOf<Pac>()
    var visiblePellets = setOf<Pellet>()


    fun computeShortestPaths(): Map<Pair<Pos, Pos>, List<Pos>> {
        return mapOf()
    }

    internal fun computeShortestPath(start: Pos, end: Pos): List<Pos> {
        if (isForbidden(start) || isForbidden(end)) {
            return listOf()
        }

        val visited = mutableSetOf<Pos>()
        val toVisit: Queue<Pair<Pos, List<Pos>>> = LinkedList()

        var current = Pair(start, listOf<Pos>())
        while (current.first != end) {
            visited.add(current.first)
            val unvisitedNeighbours = accessibleNeighbours(current.first).filterNot { visited.contains(it) }
            toVisit.addAll(unvisitedNeighbours.map { Pair(it, current.second + it) })
            current = toVisit.poll()
        }
        return current.second.dropLast(1)
    }

    internal fun accessibleNeighbours(pos: Pos): List<Pos> {
        return Direction.values().mapNotNull { direction -> move(pos, direction) }
    }

    private fun move(pos: Pos, direction: Direction): Pos? {
        val naiveNextPos = pos.move(direction)
        val x = if (naiveNextPos.x < 0) width - 1 else if (naiveNextPos.x > width - 1) 0 else naiveNextPos.x
        val result = Pos(x, naiveNextPos.y)

        return if (isForbidden(result)) null else result
    }

    fun updatePossiblePelletPositions() {
        val positionsWithoutPellet = pacs
                .map { pac: Pac ->
                    val emptyPos = pac.linesOfSight.values
                            .flatten()
                            .filter { pos: Pos -> cellType(pos) == CellContent.EMPTY }
                    emptyPos + pac.pos
                }
                .flatten()
        possiblePelletPositions.removeAll(positionsWithoutPellet)
    }

    private fun randomPossiblePelletPosition(): Pos {
        return possiblePelletPositions.random()
    }

    private fun closestPossiblePelletPosition(pos: Pos): Pos {
        logln("in closest possible pellet pos for $pos, ${possiblePelletPositions.size}")
        return possiblePelletPositions.minBy { distance(pos, it) } ?: Pos(0, 0)
    }

    fun computeLineOfSights() {
        friendlyPacs().forEach { pac -> pac.linesOfSight = lineOfSights(pac.pos) }
    }

    private fun lineOfSights(from: Pos): Map<Direction, List<Pos>> {
        return Direction.values()
                .map { direction -> direction to moveUntil(from.move(direction), direction, isForbidden) }
                .associateBy({ it.first }, { it.second })
    }

    private fun moveUntil(pos: Pos, direction: Direction, predicate: (Pos) -> Boolean, acc: List<Pos> = listOf()): List<Pos> {
        return if (predicate(pos)) {
            acc
        } else {
            moveUntil(pos.move(direction), direction, predicate, acc + pos)
        }
    }

    private fun score(lineOfSight: List<Pos>): Int {
        return lineOfSight.map { pos -> cellScore(pos) }.sum()
    }

    private fun cellScore(pos: Pos): Int {
        return when (cellType(pos)) {
            CellContent.SUPER_PELLET -> SUPER_PELLET_VALUE
            CellContent.PELLET -> SIMPLE_PELLET_VALUE
            else -> 0
        }
    }

    private fun cellType(pos: Pos): CellContent {
        val pac = pacs.find { pac -> pac.pos == pos }
        val pellet = visiblePellets.find { pellet -> pellet.pos == pos }
        return when {
            WALL_CHAR == grid[pos.y][pos.x] -> return CellContent.WALL
            pac != null -> return if (pac.mine) CellContent.FRIENDLY_PAC else CellContent.ENEMY_PAC
            pellet != null -> return if (pellet.isSuper) CellContent.SUPER_PELLET else CellContent.PELLET
            else -> CellContent.EMPTY
        }
    }

    fun friendlyPacs() = pacs.filter { it.mine }

    private fun visibleEnemyPacs() = pacs.filterNot { it.mine }

    private fun findMostRewardingPelletPosition(pac: Pac): Pos {
        val superPelletPositions = visiblePellets.filter { it.isSuper }.map { it.pos }
        val visibleSimplePelletPositions = visiblePellets.filterNot { it.isSuper }.map { it.pos }

        if (superPelletPositions.contains(pac.previousTarget)) {
            return pac.previousTarget
        }

        val previousTargets = friendlyPacs().map { it.previousTarget }
        val newTargets = friendlyPacs().map { it.target }
        val allTargets = previousTargets + newTargets


        val closestSuperPelletPos = superPelletPositions
                .filterNot { allTargets.contains(it) }
                .minBy { distance(pac.pos, it) }

        return if (closestSuperPelletPos != null) {
            closestSuperPelletPos
        } else {
            val farthestValuablePos = findFarthestValuablePos(pac.pos, pac.linesOfSight)
            return if (farthestValuablePos != null) {
                farthestValuablePos
            } else {
                val closestVisiblePelletPos = visibleSimplePelletPositions
                        .filterNot { allTargets.contains(it) }
                        .minBy { distance(pac.pos, it) }
                closestVisiblePelletPos ?: closestPossiblePelletPosition(pac.pos)
            }
        }
    }

    private fun findRunawayPosition(friendlyPac: Pac): Pos {
        val availableLinesOfSight = friendlyPac.linesOfSight
                .filterKeys { it != friendlyPac.direction() }

        val findFarthestValuablePos = findFarthestValuablePos(friendlyPac.pos, availableLinesOfSight)
        return findFarthestValuablePos ?: randomPossiblePelletPosition()
    }

    private fun findEscapeCollisionTarget(friendlyPac: Pac): Pos {
        val availableLinesOfSight = friendlyPac.linesOfSight
                .filterValues { it.none { pos -> cellType(pos) == CellContent.FRIENDLY_PAC } }

        val findFarthestValuablePos = findFarthestValuablePos(friendlyPac.pos, availableLinesOfSight)
        logln("found collision for bot ${friendlyPac.id}, previous target ${friendlyPac.previousTarget}, new target: $findFarthestValuablePos}")
        return findFarthestValuablePos ?: randomPossiblePelletPosition()
    }

    private fun findFarthestValuablePos(pos: Pos, linesOfSight: Map<Direction, List<Pos>>): Pos? {
        val bestDirection = linesOfSight
                .map { entry -> Pair(entry.key, score(entry.value)) }
                .maxBy { pair -> pair.second }!!.first

        val bestLineOfSight = linesOfSight.getValue(bestDirection)
        logln("best line of sight: $bestDirection -> $bestLineOfSight")
        return bestLineOfSight.dropLastWhile { cellScore(it) == 0 }.maxBy { distance(pos, it) }
    }

    fun computeCommand(friendlyPac: Pac): String {
        val enemyInSightPos = friendlyPac.linesOfSight.values.flatten().filter(isEnemy).minBy { distance(friendlyPac.pos, it) }
        val enemyInSight = visibleEnemyPacs().find { pac -> pac.pos == enemyInSightPos }
        val enemyWithinSafeDistance = visibleEnemyPacs().find { pac -> distance(friendlyPac.pos, pac.pos) < SAFETY_DISTANCE }
        val enemy = enemyInSight ?: enemyWithinSafeDistance

        val action = if (enemy != null) {
            if (friendlyPac.type.loseTo() == enemy.type) {
                if (!friendlyPac.canUseAbility()) {
                    val meetEnemyDirection = friendlyPac.pos.directionToMeet(enemy.pos)
                    if (friendlyPac.direction() == meetEnemyDirection) {
                        Strategy.RUNAWAY
                    } else {
                        Strategy.MOVE
                    }
                } else {
                    Strategy.SWITCH
                }
            } else {
                Strategy.MOVE
            }
        } else {
            if (friendlyPac.hasCollided()) {
                Strategy.COLLISION
            } else {
                val moreSuperPellets = visiblePellets.any() { it.isSuper }
                if (moreSuperPellets && friendlyPac.canUseAbility()) {
                    Strategy.SPEED
                } else {
                    Strategy.MOVE
                }
            }
        }

        val prefix = "${action.command} ${friendlyPac.id}"

        logln("compute command for pac ${friendlyPac.id}: $action")

        friendlyPac.target = when (action) {
            Strategy.MOVE, Strategy.SPEED -> findMostRewardingPelletPosition(friendlyPac)
            Strategy.RUNAWAY -> findRunawayPosition(friendlyPac)
            Strategy.SWITCH -> friendlyPac.previousTarget
            Strategy.COLLISION -> findEscapeCollisionTarget(friendlyPac)
        }

        logln("computed target for pac ${friendlyPac.id}: ${friendlyPac.target}")

        val command = when (action) {
            Strategy.SWITCH -> {
                "$prefix ${enemy?.type?.loseTo()}"
            }
            Strategy.SPEED -> {
                prefix
            }
            else -> {
                "$prefix ${friendlyPac.target.x} ${friendlyPac.target.y}"
            }
        }

        logln("computed command for pac ${friendlyPac.id}: $command")

        return "$command $action"
    }

    fun display() {
        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, _ ->
                val c = when (cellType(Pos(colIndex, rowIndex))) {
                    CellContent.WALL -> '#'
                    CellContent.FRIENDLY_PAC -> 'M'
                    CellContent.ENEMY_PAC -> 'X'
                    CellContent.PELLET -> 'o'
                    CellContent.SUPER_PELLET -> 'O'
                    else -> ' '
                }
                log(c.toString())
            }
            logln("")
        }
    }


}

/**
 * Grab the pellets as fast as you can!
 **/
fun main() {

    // Read init inputs
    val input = Scanner(System.`in`)
    val width = input.nextInt() // size of the grid
    val height = input.nextInt() // top left corner is (x=0, y=0)
    if (input.hasNextLine()) {
        input.nextLine()
    }

    val grid = Array(height) { "" }
    for (rowIndex in 0 until height) {
        val row = input.nextLine() // one line of the grid: space " " is floor, pound "#" is wall
        grid[rowIndex] = row
    }
    val game = Game(width, height, grid)
    var turn = 0

    game.display()

    // game loop
    while (true) {
        turn++

        game.score = input.nextInt()
        game.opponentScore = input.nextInt()

        val visiblePacCount = input.nextInt() // all your pacs and enemy pacs in sight
        val pacs = mutableSetOf<Pac>()
        for (i in 0 until visiblePacCount) {
            val pacId = input.nextInt() // pac number (unique within a team)
            val mine = input.nextInt() != 0 // true if this pac is yours
            val newPos = Pos(input.nextInt(), input.nextInt())
            val typeId = input.next() // unused in wood leagues
            val speedTurnsLeft = input.nextInt() // unused in wood leagues
            val abilityCooldown = input.nextInt() // unused in wood leagues

            val type = PacType.valueOf(typeId)
            val previousPac = game.pacs.find { it.id == pacId }
            val previousPos = previousPac?.pos ?: Pos(0, 0)
            val previousTarget = previousPac?.target ?: Pos(0, 0)
            pacs.add(Pac(pacId, mine, newPos, previousPos, type, abilityCooldown, previousTarget))
        }
        game.pacs = pacs

        val visiblePelletCount = input.nextInt() // all pellets in sight
        val pellets = mutableSetOf<Pellet>()
        for (i in 0 until visiblePelletCount) {
            val x = input.nextInt()
            val y = input.nextInt()
            val value = input.nextInt() // amount of points this pellet is worth
            pellets.add(Pellet(Pos(x, y), value))
        }
        game.visiblePellets = pellets

        game.computeLineOfSights()

        // Update possible pellets
        game.updatePossiblePelletPositions()

        // Plan PACs commands
        val command = game.friendlyPacs().joinToString("|") { game.computeCommand(it) }

        println(command) // MOVE <pacId> <x> <y>
    }
}