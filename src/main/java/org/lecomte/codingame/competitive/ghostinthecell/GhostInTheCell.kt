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

class Factories(size: Int) : Iterable<Factory> {
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

    fun getDistance(factory1: Factory, factory2: Factory) = distances[factory1.id][factory2.id]

    fun factoriesByDistance(id: Int): List<Pair<Int?, Factory>> {
        return distances[id]
                .mapIndexed { index, distance -> Pair(distance, get(index)) }
                .filter { pair -> pair.first != null }
                .sortedBy { pair -> pair.first }
    }

    fun matchingFactoriesOrderByDistance(factory: Factory, predicate: (Factory) -> Boolean): List<Factory> {
        return factoriesByDistance(factory.id)
                .map { pair -> pair.second }
                .filter(predicate)
    }

    fun closestFactoryMatching(factory: Factory, predicate: (Factory) -> Boolean): Factory? {
        return factoriesByDistance(factory.id)
                .map { pair -> pair.second }
                .find(predicate)
    }

    fun productiveNeutralFactoriesOrderByDistance(factory: Factory) =
            matchingFactoriesOrderByDistance(factory) { it.owner == FactoryOwner.NEUTRAL && it.production > 0 }

    fun productiveEnemyFactoriesOrderByDistance(factory: Factory) =
            matchingFactoriesOrderByDistance(factory) { it.owner == FactoryOwner.OPPONENT && it.production > 0 }

    fun closestProductiveOpponentFactory(factory: Factory): Factory? = closestFactoryMatching(factory) { it.owner == FactoryOwner.OPPONENT && it.production > 0 }

    fun closestProductiveNeutralFactory(factory: Factory): Factory? = closestFactoryMatching(factory) { it.owner == FactoryOwner.NEUTRAL && it.production > 0 }

    fun displayFactories() {
        content.forEach { factory -> System.err.println(factory) }

    }

    fun displayDistances() {
        distances.forEachIndexed { i, edges -> System.err.println("Factory $i : ${edges.joinToString(" ")}") }
    }

    override fun iterator(): Iterator<Factory> {
        return content.iterator()
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
        factories.addDistance(factory1, factory2, distance)
    }

    // game loop
    while (true) {
        val entityCount = input.nextInt() // the number of entities (e.g. factories and troops)

        // Read inputs
        for (i in 0 until entityCount) {
            val entityId = input.nextInt()
            val entityType = input.next()
            val arg1 = input.nextInt()
            val arg2 = input.nextInt()
            val arg3 = input.nextInt()
            val arg4 = input.nextInt()
            val arg5 = input.nextInt()

            if (entityType == FACTORY_TYPE) {
                System.err.println("Factory $entityId, owner: $arg1, stock: $arg2, production $arg3")
                val factory = factories.get(entityId)
                factory.setOwner(arg1)
                factory.stock = arg2
                factory.production = arg3
            } else if (entityType == TROOP_TYPE) {
                // TODO something here one day
            }
        }

        // Plan attacks
        val attacks = factories
                .filter { it.owner == FactoryOwner.ME }
                .flatMap { factory ->
                    factories.productiveNeutralFactoriesOrderByDistance(factory)
                            .union(factories.productiveEnemyFactoriesOrderByDistance(factory))
                            .map { targetFactory -> Triple(factory, targetFactory, targetFactory.stock) }
                }

        // Display outputs
        val message = if (attacks.isEmpty())
            "WAIT"
        else
            attacks.joinToString(";") { triple -> "MOVE ${triple.first.id} ${triple.second.id} ${triple.third}" }

        println(message)
    }
}