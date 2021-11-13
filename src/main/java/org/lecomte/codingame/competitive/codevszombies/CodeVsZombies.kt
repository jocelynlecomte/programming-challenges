package org.lecomte.codingame.competitive.codevszombies

import java.util.*

const val MAX_X = 16000
const val MAX_Y = 9000

data class Position(val x: Int, val y: Int) {
    fun squaredLength(o: Position) = (x - o.x) * (x - o.x) + (y - o.y) * (y - o.y)
}

data class Human(val id: Int, val pos: Position)

data class Zombie(val id: Int, val pos: Position, val nextPos: Position)

class State(val ash: Position, val humans: Array<Human>, val zombies: Array<Zombie>) {
    fun computeTarget(): Position {
        return zombies.map { it.pos }.minByOrNull { ash.squaredLength(it) }!!
    }
}

/**
 * Save humans, destroy zombies!
 **/
fun main() {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {
        val ash = Position(input.nextInt(), input.nextInt())
        val humanCount = input.nextInt()
        val humans = Array(humanCount) {
            Human(input.nextInt(), Position(input.nextInt(), input.nextInt()))
        }
        val zombieCount = input.nextInt()
        val zombies = Array(zombieCount) {
            Zombie(
                input.nextInt(),
                Position(input.nextInt(), input.nextInt()),
                Position(input.nextInt(), input.nextInt())
            )
        }

        val state = State(ash, humans, zombies)

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");
        val target = state.computeTarget()
        println("${target.x} ${target.y}") // Your destination coordinates
    }
}
