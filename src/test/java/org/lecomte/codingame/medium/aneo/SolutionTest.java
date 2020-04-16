package org.lecomte.codingame.medium.aneo;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class SolutionTest {
    @Test
    public void le_feu_du_village() {
        InputStream is = getClass().getResourceAsStream("01.txt");
        List<Integer> result = Solution.solve(is).stream().map(outputLine -> outputLine.values.get(0)).collect(toList());

        assertThat(result).containsOnly(50);
    }

    @Test
    public void le_feu_du_village_2() {
        InputStream is = getClass().getResourceAsStream("02.txt");
        List<Integer> result = Solution.solve(is).stream().map(outputLine -> outputLine.values.get(0)).collect(toList());

        assertThat(result).containsOnly(36);
    }

    @Test
    public void feux_rapides() {
        InputStream is = getClass().getResourceAsStream("10.txt");
        List<Integer> result = Solution.solve(is).stream().map(outputLine -> outputLine.values.get(0)).collect(toList());

        assertThat(result).containsOnly(74);
    }
}