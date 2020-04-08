package org.lecomte.codingame.easy.ghostlegs;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class SolutionTest {
    @Test
    public void simple_sample() {
        InputStream is = getClass().getResourceAsStream("01.txt");
        List<String> result = Solution.solve(is).stream().map(outputLine -> outputLine.values.get(0)).collect(toList());

        assertThat(result).containsExactly("A2", "B1", "C3");
    }
}