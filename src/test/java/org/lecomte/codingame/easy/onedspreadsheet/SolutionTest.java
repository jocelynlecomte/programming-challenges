package org.lecomte.codingame.easy.onedspreadsheet;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class SolutionTest {
    @Test
    public void simple_dependency() {
        InputStream is = getClass().getResourceAsStream("01.txt");
        List<Integer> result = Solution.solve(is).stream().map(outputLine -> outputLine.values.get(0)).collect(toList());

        assertThat(result).containsExactly(3, 7);
    }

    @Test
    public void accounting_is_easy() {
        InputStream is = getClass().getResourceAsStream("06.txt");
        List<Integer> result = Solution.solve(is).stream().map(outputLine -> outputLine.values.get(0)).collect(toList());

        assertThat(result).containsExactly(144, 156, 12, 24, 288, 12);
    }
}