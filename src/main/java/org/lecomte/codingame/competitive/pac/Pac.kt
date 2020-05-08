package org.lecomte.codingame.competitive.pac

import java.util.*

const val WALL_CHAR = '#'
const val EMPTY_CHAR = ' '
const val SIMPLE_PELLET_VALUE = 1
const val SUPER_PELLET_VALUE = 10
const val SQUARED_SAFETY_DISTANCE = 4

fun log(message: String?) {
    System.err.print(message)
}

fun logln(message: String?) {
    System.err.println(message)
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

data class Point(val x: Int, val y: Int) {
    fun squaredDistance(other: Point) = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)

    fun move(direction: Direction): Point {
        return when (direction) {
            Direction.NORTH -> Point(x, y - 1)
            Direction.SOUTH -> Point(x, y + 1)
            Direction.EAST -> Point(x + 1, y)
            Direction.WEST -> Point(x - 1, y)
        }
    }

    /**
     * computes the direction this point should go to move to the other point
     */
    fun directionToMeet(other: Point): Direction = when {
        x > other.x -> Direction.WEST
        x < other.x -> Direction.EAST
        y > other.y -> Direction.NORTH
        else -> Direction.SOUTH
    }
}

data class Pellet(val pos: Point, val value: Int) {
    val isSuper = value > 1
}

data class Pac(val id: Int, val mine: Boolean, val pos: Point, val previousPos: Point, val type: PacType,
               val abilityCooldown: Int, val previousTarget: Point) {

    var target: Point = Point(0, 0)
    var linesOfSight = mapOf<Direction, List<Point>>()

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
    private val possiblePelletPositions = mutableSetOf<Point>()

    init {
        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, char ->
                if (char == EMPTY_CHAR) possiblePelletPositions.add(Point(colIndex, rowIndex))
            }
        }
    }

    var score = 0
    var opponentScore = 0

    var pacs = setOf<Pac>()
    var visiblePellets = setOf<Pellet>()

    private val isForbidden: (Point) -> Boolean = { pos -> pos.x < 0 || pos.x > width - 1 || pos.y < 0 || pos.y > height - 1 || cellType(pos) == CellContent.WALL }
    private val isEnemy: (Point) -> Boolean = { pos -> cellType(pos) == CellContent.ENEMY_PAC }

    fun updatePossiblePelletPositions() {
        val positionsWithoutPellet = pacs
                .map { pac: Pac ->
                    val emptyPos = pac.linesOfSight.values
                            .flatten()
                            .filter { pos: Point -> cellType(pos) == CellContent.EMPTY }
                    emptyPos + pac.pos
                }
                .flatten()
        possiblePelletPositions.removeAll(positionsWithoutPellet)
        logln("possible pellet positions ${possiblePelletPositions.size}")
    }

    private fun randomPossiblePelletPosition(): Point {
        return possiblePelletPositions.random()
    }

    private fun closestPossiblePelletPosition(pos: Point): Point {
        return possiblePelletPositions.minBy(pos::squaredDistance) ?: Point(0, 0)
    }

    fun computeLineOfSights() {
        friendlyPacs().forEach { pac -> pac.linesOfSight = lineOfSights(pac.pos) }
    }

    private fun lineOfSights(from: Point): Map<Direction, List<Point>> {
        return Direction.values()
                .map { direction -> direction to moveUntil(from.move(direction), direction, isForbidden) }
                .associateBy({ it.first }, { it.second })
    }

    private fun moveUntil(pos: Point, direction: Direction, predicate: (Point) -> Boolean, acc: List<Point> = listOf()): List<Point> {
        return if (predicate(pos)) {
            acc
        } else {
            moveUntil(pos.move(direction), direction, predicate, acc + pos)
        }
    }

    private fun score(lineOfSight: List<Point>): Int {
        return lineOfSight.map { pos -> cellScore(pos) }.sum()
    }

    private fun cellScore(point: Point): Int {
        return when (cellType(point)) {
            CellContent.SUPER_PELLET -> SUPER_PELLET_VALUE
            CellContent.PELLET -> SIMPLE_PELLET_VALUE
            else -> 0
        }
    }

    private fun cellType(point: Point): CellContent {
        val pac = pacs.find { pac -> pac.pos == point }
        val pellet = visiblePellets.find { pellet -> pellet.pos == point }
        return when {
            WALL_CHAR == grid[point.y][point.x] -> return CellContent.WALL
            pac != null -> return if (pac.mine) CellContent.FRIENDLY_PAC else CellContent.ENEMY_PAC
            pellet != null -> return if (pellet.isSuper) CellContent.SUPER_PELLET else CellContent.PELLET
            else -> CellContent.EMPTY
        }
    }

    fun friendlyPacs() = pacs.filter { it.mine }

    private fun visibleEnemyPacs() = pacs.filterNot { it.mine }

    private fun findMostRewardingPelletPosition(pac: Pac): Point {
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
                .minBy(pac.pos::squaredDistance)

        return if (closestSuperPelletPos != null) {
            closestSuperPelletPos
        } else {
            val farthestValuablePos = findFarthestValuablePos(pac.pos, pac.linesOfSight)
            return if (farthestValuablePos != null) {
                farthestValuablePos
            } else {
                val closestVisiblePelletPos = visibleSimplePelletPositions
                        .filterNot { allTargets.contains(it) }
                        .minBy(pac.pos::squaredDistance)
                closestVisiblePelletPos ?: closestPossiblePelletPosition(pac.pos)
            }
        }
    }

    private fun findRunawayPosition(friendlyPac: Pac): Point {
        val availableLinesOfSight = friendlyPac.linesOfSight
                .filterKeys { it != friendlyPac.direction() }

        val findFarthestValuablePos = findFarthestValuablePos(friendlyPac.pos, availableLinesOfSight)
        return findFarthestValuablePos ?: randomPossiblePelletPosition()
    }

    private fun findEscapeCollisionTarget(friendlyPac: Pac): Point {
        val availableLinesOfSight = friendlyPac.linesOfSight
                .filterValues { it.none { pos -> cellType(pos) == CellContent.FRIENDLY_PAC } }

        val findFarthestValuablePos = findFarthestValuablePos(friendlyPac.pos, availableLinesOfSight)
        logln("found collision for bot ${friendlyPac.id}, previous target ${friendlyPac.previousTarget}, new target: $findFarthestValuablePos}")
        return findFarthestValuablePos ?: randomPossiblePelletPosition()
    }

    private fun findFarthestValuablePos(pos: Point, linesOfSight: Map<Direction, List<Point>>): Point? {
        val bestDirection = linesOfSight
                .map { entry -> Pair(entry.key, score(entry.value)) }
                .maxBy { pair -> pair.second }!!.first

        val bestLineOfSight = linesOfSight.getValue(bestDirection)
        return bestLineOfSight.dropLastWhile { cellScore(it) == 0 }.maxBy(pos::squaredDistance)
    }

    fun computeCommand(friendlyPac: Pac): String {
        val enemyInSightPos = friendlyPac.linesOfSight.values.flatten().filter(isEnemy).minBy(friendlyPac.pos::squaredDistance)
        val enemyInSight = visibleEnemyPacs().find { pac -> pac.pos == enemyInSightPos }
        val enemyWithinSafeDistance = visibleEnemyPacs().find { pac -> friendlyPac.pos.squaredDistance(pac.pos) < SQUARED_SAFETY_DISTANCE }
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

        friendlyPac.target = when (action) {
            Strategy.MOVE, Strategy.SPEED -> findMostRewardingPelletPosition(friendlyPac)
            Strategy.RUNAWAY -> findRunawayPosition(friendlyPac)
            Strategy.SWITCH -> friendlyPac.previousTarget
            Strategy.COLLISION -> findEscapeCollisionTarget(friendlyPac)
        }

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
        return "$command $action"
    }

    fun display() {
        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, _ ->
                val c = when (cellType(Point(colIndex, rowIndex))) {
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
fun main(args: Array<String>) {

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
            val newPos = Point(input.nextInt(), input.nextInt())
            val typeId = input.next() // unused in wood leagues
            val speedTurnsLeft = input.nextInt() // unused in wood leagues
            val abilityCooldown = input.nextInt() // unused in wood leagues

            val type = PacType.valueOf(typeId)
            val previousPac = game.pacs.find { it.id == pacId }
            val previousPos = previousPac?.pos ?: Point(0, 0)
            val previousTarget = previousPac?.target ?: Point(0, 0)
            pacs.add(Pac(pacId, mine, newPos, previousPos, type, abilityCooldown, previousTarget))
        }
        game.pacs = pacs

        val visiblePelletCount = input.nextInt() // all pellets in sight
        val pellets = mutableSetOf<Pellet>()
        for (i in 0 until visiblePelletCount) {
            val x = input.nextInt()
            val y = input.nextInt()
            val value = input.nextInt() // amount of points this pellet is worth
            pellets.add(Pellet(Point(x, y), value))
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