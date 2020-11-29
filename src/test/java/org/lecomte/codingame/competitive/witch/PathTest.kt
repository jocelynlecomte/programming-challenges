package org.lecomte.codingame.competitive.witch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PathTest {

    @Test
    fun compact() {
        // ABBAABABB -> A1 B2 A1 A1 B1 A1 B2
        val spellA = Spell(1, Pos(0, 0, 0, 0), false)
        val spellB = Spell(2, Pos(0, 0, 0, 0), true)
        val path = Path(listOf(spellA, spellB, spellB, spellA, spellA, spellB, spellA, spellB, spellB))
        val compactedPath = listOf(Pair(spellA, 1), Pair(spellB, 2), Pair(spellA, 1), Pair(spellA, 1), Pair(spellB, 1),
                Pair(spellA, 1), Pair(spellB, 2))
        assertThat(path.compact()).isEqualTo(compactedPath)
    }
}