package org.lecomte.codingame.easy.rpcls;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SolutionTest {

    @Test
    public void same_as_example() {
        InputStream is = getClass().getResourceAsStream("01.txt");
        List<OutputLine<Integer>> outputLines = Solution.solve(is);

        assertThat(outputLines.get(0).values).isEqualTo(List.of(2));
        assertThat(outputLines.get(1).values).isEqualTo(List.of(6, 5, 1));
    }
}