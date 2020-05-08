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
            ###############################
            #       #   #     #   #       #
            # ### # # # # ### # # # # ### #
            # ### #   #         #   # ### #
            # ### ### ### ### ### ### ### #
            #     #   #         #   #     #
            ##### # # # # ### # # # # #####
                    #   #     #   #        
            ### # # ### ####### ### # # ###
            #   # #                 # #   #
            # ### ### ##### ##### ### ### #
            #         #         #         #
            ##### ### # # # # # # ### #####
            #     #       # #       #     #
            # # # # # # ####### # # # # # #
            ###############################
        """.trimIndent()
        val grid = gridAsText.split(delimiters = *arrayOf("\n")).toTypedArray()
        game = Game(31, 16, grid)
    }

    @Nested
    inner class ComputeLinesOfSight {
        @Test
        fun `should be OK when there is 1 direction`() {
            val pac = Pac(0, true, Point(29, 14), Point(0, 0), PacType.PAPER, 0, Point(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).containsExactly(Point(29, 13))
        }

        @Test
        fun `should be OK when there are 2 directions`() {
            val pac = Pac(0, true, Point(29, 13), Point(0, 0), PacType.PAPER, 0, Point(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEqualTo(horizontal(13, 28, 25))
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEmpty()
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).containsExactly(Point(29, 14))
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).isEmpty()
        }

        @Test
        fun `should be OK when there are 3 directions`() {
            val pac = Pac(0, true, Point(21, 11), Point(0, 0), PacType.PAPER, 0, Point(0, 0))
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
            val pac = Pac(0, true, Point(25, 7), Point(0, 0), PacType.PAPER, 0, Point(0, 0))
            game.pacs = setOf(pac)

            game.computeLineOfSights()

            assertThat(pac.linesOfSight).hasSize(4)
            assertThat(pac.linesOfSight.getValue(Direction.WEST)).isEqualTo(horizontal(7, 24, 23))
            assertThat(pac.linesOfSight.getValue(Direction.EAST)).isEqualTo(horizontal(7, 26, 30))
            assertThat(pac.linesOfSight.getValue(Direction.SOUTH)).isEqualTo(vertical(25, 8, 14))
            assertThat(pac.linesOfSight.getValue(Direction.NORTH)).isEqualTo(vertical(25, 6, 1))
        }
    }

    private fun horizontal(row: Int, colStart: Int, colEnd: Int): List<Point> {
        val range = if (colStart <= colEnd)
            IntRange(colStart, colEnd)
        else
            IntRange(colEnd, colStart).reversed()
        return range.map { Point(it, row) }
    }

    private fun vertical(col: Int, rowStart: Int, rowEnd: Int): List<Point> {
        val range = if (rowStart <= rowEnd)
            IntRange(rowStart, rowEnd)
        else
            IntRange(rowEnd, rowStart).reversed()
        return range.map { Point(col, it) }
    }
}