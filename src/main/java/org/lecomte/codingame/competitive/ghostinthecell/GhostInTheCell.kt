package org.lecomte.codingame.competitive.ghostinthecell

import java.util.*

const val FACTORY_TYPE = "FACTORY"
const val TROOP_TYPE = "TROOP"

class Factories(size: Int) {
    private val adjacencyMatrix = Array(size) { IntArray(size) }

    fun addEdge(node1: Int, node2: Int, weight: Int) {
        adjacencyMatrix[node1][node2] = weight
    }

    fun display() {
        adjacencyMatrix.forEachIndexed { i, edges -> System.err.println("Factory $i : ${edges.joinToString(" ")}") }
    }
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val factoryCount = input.nextInt() // the number of factories
    val factories = Factories(factoryCount)

    val linkCount = input.nextInt() // the number of links between factories
    for (i in 0 until linkCount) {
        val factory1 = input.nextInt()
        val factory2 = input.nextInt()
        val distance = input.nextInt()
        factories.addEdge(factory1, factory2, distance)
        factories.addEdge(factory2, factory1, distance)
    }

    factories.display()

    // game loop
    while (true) {
        val entityCount = input.nextInt() // the number of entities (e.g. factories and troops)
        var sourceFactory: Int? = null
        var destinationFactory: Int? = null
        var cyborgsInFactory: Int? = null
        for (i in 0 until entityCount) {
            val entityId = input.nextInt()
            val entityType = input.next()
            val arg1 = input.nextInt()
            val arg2 = input.nextInt()
            val arg3 = input.nextInt()
            val arg4 = input.nextInt()
            val arg5 = input.nextInt()

            if (entityType == FACTORY_TYPE) {
                if (arg1 == 1) {
                    sourceFactory = entityId
                    cyborgsInFactory = arg2
                } else if (arg1 == -1) {
                    destinationFactory = entityId
                }
            }
        }

        val message = if (sourceFactory != null && destinationFactory != null)
            "MOVE $sourceFactory $destinationFactory $cyborgsInFactory"
        else "WAIT"

        println(message)
    }
}