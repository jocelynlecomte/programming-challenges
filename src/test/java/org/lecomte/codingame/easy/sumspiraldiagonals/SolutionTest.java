package org.lecomte.codingame.easy.sumspiraldiagonals;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class SolutionTest {
    @Test
    public void odd_spiral() {
        InputStream is = getClass().getResourceAsStream("01_odd_spiral.txt");
        long result = Solution.solve(is);

        assertThat(result).isEqualTo(133);
    }

    @Test
    public void even_spiral() {
        InputStream is = getClass().getResourceAsStream("02_even_spiral.txt");
        long result = Solution.solve(is);

        assertThat(result).isEqualTo(61584);
    }

    @Test
    public void milky_way() {
        InputStream is = getClass().getResourceAsStream("04_milky_way.txt");
        long result = Solution.solve(is);

        assertThat(result).isEqualTo(4086949725L);
    }
}
