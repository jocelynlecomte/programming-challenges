package org.lecomte.codingame.competitive.ghostinthecell

import java.util.*

const val FACTORY_TYPE = "FACTORY"
const val TROOP_TYPE = "TROOP"

enum class FactoryOwner {
    ME, NEUTRAL, OPPONENT
}

data class Factory(val id: Int) {
    var production = 0
    var stock = 0
    var owner = FactoryOwner.NEUTRAL

    fun setOwner(value: Int) {
        owner = if (value == -1) FactoryOwner.OPPONENT else if (value == 0) FactoryOwner.NEUTRAL else FactoryOwner.ME
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Factory

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "Factory(id=$id, production=$production, stock=$stock, owner=$owner)"
    }


}

class Factories(size: Int) {
    private val content: MutableSet<Factory> = mutableSetOf()
    private val distances = Array(size) { Array<Int?>(size) { null } }

    init {
        for (i in 0 until size) {
            content.add(Factory(i))
        }
    }

    fun get(id: Int): Factory {
        return content.single { factory -> factory.id == id }
    }

    fun addDistance(id1: Int, id2: Int, distance: Int) {
        distances[id1][id2] = distance
        distances[id2][id1] = distance
    }

    fun factoriesByDistance(id: Int): List<Pair<Int?, Factory>> {
        return distances[id]
                .mapIndexed { index, distance -> Pair(distance, get(index)) }
                .filter { pair -> pair.first != null }
                .sortedBy { pair -> pair.first }
    }

    fun closestFactoryMatching(factory: Factory, predicate: (Factory) -> Boolean): Factory? {
        return factoriesByDistance(factory.id)
                .map { pair -> pair.second }
                .find(predicate)
    }

    fun closestOpponentFactory(factory: Factory): Factory? = closestFactoryMatching(factory) { it.owner == FactoryOwner.OPPONENT }

    fun closestNeutralFactory(factory: Factory): Factory? = closestFactoryMatching(factory) { it.owner == FactoryOwner.NEUTRAL }

    fun display() {
        content.forEach { factory -> System.err.println(factory) }
        distances.forEachIndexed { i, edges -> System.err.println("Factory $i : ${edges.joinToString(" ")}") }
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
        //System.err.println("$factoryCount $factory1, $factory2, $distance")
        factories.addDistance(factory1, factory2, distance)
    }

    factories.display()

    // game loop
    while (true) {
        val entityCount = input.nextInt() // the number of entities (e.g. factories and troops)
        val attacks = mutableListOf<Pair<Factory, Factory>>()

        for (i in 0 until entityCount) {
            val entityId = input.nextInt()
            val entityType = input.next()
            val arg1 = input.nextInt()
            val arg2 = input.nextInt()
            val arg3 = input.nextInt()
            val arg4 = input.nextInt()
            val arg5 = input.nextInt()

            if (entityType == FACTORY_TYPE) {
                val factory = factories.get(entityId)
                factory.setOwner(arg1)
                factory.stock = arg2
                factory.production = arg3
                when (factory.owner) {
                    FactoryOwner.ME -> {
                        val factoryToAttack = factories.closestNeutralFactory(factory)
                                ?: factories.closestOpponentFactory(factory)
                        if (factoryToAttack != null) {
                            attacks.add(Pair(factory, factoryToAttack))
                        }
                    }
                    FactoryOwner.OPPONENT -> {
                        // TODO OPPONENT
                    }
                    else -> {
                        // TODO NEUTRAL
                    }
                }
            } else if (entityType == TROOP_TYPE) {
                // TODO something here one day
            }
        }

        System.err.println("${attacks.size} attacks planned")
        val message = if (attacks.isEmpty())
            "WAIT"
        else
            attacks.joinToString(";") { pair -> "MOVE ${pair.first.id} ${pair.second.id} ${pair.first.stock}" }

        println(message)
    }
}