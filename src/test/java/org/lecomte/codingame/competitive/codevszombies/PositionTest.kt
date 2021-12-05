package org.lecomte.codingame.competitive.codevszombies

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class PositionTest {
    @Test
    fun `dfgfdg`() {
        val origin = Position(0, 0)
        val target = Position(8250, 8999)

        Assertions.assertThat(origin.nextPos(target, ASH_SPEED)).isEqualTo(Position(675, 737))
    }
}
