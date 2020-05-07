package org.lecomte.codingame.competitive.ghostinthecell

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.lecomte.codingame.competitive.ghostinthecell.FactoryOwner.OPPONENT

internal class FactoriesTest {
    private lateinit var factories: Factories

    @BeforeEach
    fun setup() {
        factories = Factories(5)
        factories.get(1).owner = OPPONENT
        factories.get(1).production = 1

        factories.addDistance(0, 1, 5)
        factories.addDistance(0, 2, 3)
        factories.addDistance(0, 4, 8)
        factories.addDistance(1, 3, 4)
        factories.addDistance(1, 4, 1)
        factories.addDistance(2, 3, 7)
        factories.addDistance(2, 4, 9)
        factories.addDistance(3, 3, 2)
    }

    @Nested
    inner class Get {
        @Test
        fun `should return proper value when id exists`() {
            assertThat(factories.get(1)).extracting("id").isEqualTo(1)
        }

        @Test
        fun `should throw when id does not exist`() {
            assertThrows(NoSuchElementException::class.java) { factories.get(5) }
        }
    }

    @Nested
    inner class FactoriesByDistance {
        @Test
        fun `should return factories sorted by distance`() {
            assertThat(factories.factoriesByDistance(0)).containsExactly(
                    Pair(3, factories.get(2)),
                    Pair(5, factories.get(1)),
                    Pair(8, factories.get(4))
            )
        }
    }

    @Nested
    inner class ClosestProductiveOpponentFactory {
        @Test
        fun `should return the closest productive opponent when exists`() {
            assertThat(factories.closestProductiveOpponentFactory(factories.get(0))).isEqualTo(factories.get(1))
        }

        @Test
        fun `should return null when there is no productive opponent factory`() {
            assertThat(factories.closestProductiveOpponentFactory(factories.get(1))).isNull()
        }
    }
}