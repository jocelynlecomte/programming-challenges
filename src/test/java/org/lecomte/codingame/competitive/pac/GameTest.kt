package org.lecomte.codingame.competitive.pac

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameTest {
    private lateinit var game: Game

    @BeforeEach
    fun setup() {
        val gridAsText = """
            0123456789012345678901234567890
            ###############################0
            #       #   #     #   #       #1
            # ### # # # # ### # # # # ### #2
            # ### #   #         #   # ### #3
            # ### ### ### ### ### ### ### #4
            #     #   #         #   #     #5
            ##### # # # # ### # # # # #####6
                    #   #     #   #        7
            ### # # ### ####### ### # # ###8
            #   # #                 # #   #9
            # ### ### ##### ##### ### ### #0
            #         #         #         #1
            ##### ### # # # # # # ### #####2
            #     #       # #       #     #3
            # # # # # # ####### # # # # # #4
            ###############################5
        """.trimIndent()
        val grid = gridAsText.split(delimiters = *arrayOf("\n")).map { it.dropLast(1) }.drop(1).toTypedArray()
        game = Game(31, 16, grid)
    }

    @Nested
    inner class TestNeighbours {
        @Test
        fun `should be OK for simple neighbours`() {
            assertThat(game.accessibleNeighbours(Pos(1, 14))).contains(Pos(1, 13))
            assertThat(game.accessibleNeighbours(Pos(1, 1))).contains(Pos(2, 1), Pos(1, 2))
            assertThat(game.accessibleNeighbours(Pos(5, 7))).contains(Pos(5, 6), Pos(6, 7), Pos(5, 8), Pos(4, 7))
        }

        @Test
        fun `should be OK for neighbours via teleport`() {
            assertThat(game.accessibleNeighbours(Pos(0, 7))).contains(Pos(1, 7), Pos(30, 7))
            assertThat(game.accessibleNeighbours(Pos(30, 7))).contains(Pos(29, 7), Pos(0, 7))
        }
    }

    @Nested
    inner class ComputeShortestPath {
        @Test
        fun `should be empty for incorrect input or output`() {
            assertThat(game.computeShortestPath(Pos(0, 0), Pos(1, 2))).isEmpty()
            assertThat(game.computeShortestPath(Pos(1, 2), Pos(0, 0))).isEmpty()
        }

        @Test
        fun `should be empty for immediate neighbours`() {
            assertThat(game.computeShortestPath(Pos(1, 1), Pos(1, 2))).isEmpty()
            assertThat(game.computeShortestPath(Pos(1, 1), Pos(2, 1))).isEmpty()
        }

        @Test
        fun `should be OK for random accessible point`() {
            assertThat(game.computeShortestPath(Pos(1, 1), Pos(7, 1))).isEqualTo(horizontal(1, 2, 6))
            assertThat(game.computeShortestPath(Pos(8, 5), Pos(8, 9))).isEqualTo(vertical(7, 5, 9))
        }

        @Test
        fun `should be OK for random accessible point through teleport`() {
            assertThat(game.computeShortestPath(Pos(5, 7), Pos(25, 7))).isEqualTo(horizontal(7, 4, 0) + horizontal(7, 30, 26))
        }

        @Test
        fun `should be equal regarding to horizontal symetry`() {
            val middleRow = 31 / 2
            val path = game.computeShortestPath(Pos(1, 1), Pos(middleRow, 1))
            val translatedPath = path.map { (x, y) -> Pos(2 * middleRow - x, y) }
            val symetricPath = game.computeShortestPath(Pos(29, 1), Pos(middleRow, 1))
            assertThat(translatedPath).isEqualTo(symetricPath)
        }
    }

    @Nested
    inner class ComputeLinesOfSight {
        @Test
        fun `should be OK when there is 1 direction`() {
            val pac = Pac(0, true, Pos(29, 14), Pos(0, 0), PacType.PAPER, 0, Pos(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).containsExactly(Pos(29, 13))
        }

        @Test
        fun `should be OK when there are 2 directions`() {
            val pac = Pac(0, true, Pos(29, 13), Pos(0, 0), PacType.PAPER, 0, Pos(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEqualTo(horizontal(13, 28, 25))
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).containsExactly(Pos(29, 14))
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).isEmpty()
        }

        @Test
        fun `should be OK when there are 3 directions`() {
            val pac = Pac(0, true, Pos(21, 11), Pos(0, 0), PacType.PAPER, 0, Pos(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEqualTo(horizontal(11, 22, 29))
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).isEqualTo(vertical(21, 12, 14))
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).isEqualTo(vertical(21, 10, 9))
        }

        @Test
        fun `should be OK when there are 4 directions`() {
            val pac = Pac(0, true, Pos(25, 7), Pos(0, 0), PacType.PAPER, 0, Pos(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEqualTo(horizontal(7, 24, 23))
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEqualTo(horizontal(7, 26, 30))
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).isEqualTo(vertical(25, 8, 14))
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).isEqualTo(vertical(25, 6, 1))
        }
    }

    private fun horizontal(row: Int, colStart: Int, colEnd: Int): List<Pos> {
        val range = if (colStart <= colEnd)
            IntRange(colStart, colEnd)
        else
            IntRange(colEnd, colStart).reversed()
        return range.map { Pos(it, row) }
    }

    private fun vertical(col: Int, rowStart: Int, rowEnd: Int): List<Pos> {
        val range = if (rowStart <= rowEnd)
            IntRange(rowStart, rowEnd)
        else
            IntRange(rowEnd, rowStart).reversed()
        return range.map { Pos(col, it) }
    }
}