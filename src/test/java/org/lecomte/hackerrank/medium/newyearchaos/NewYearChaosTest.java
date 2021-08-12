package org.lecomte.hackerrank.medium.newyearchaos;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class NewYearChaosTest {

    @Test
    void minimumBribes00_01() {
        filesAssert("Input_00_01.txt", "Output_00_01.txt");
    }

    @Test
    void minimumBribes01_02() {
        filesAssert("Input_01_02.txt", "Output_01_02.txt");
    }

    private void filesAssert(String inputFileName, String outputFileName) {
        InputStream inputStream = getClass().getResourceAsStream(inputFileName);
        InputStream expectedStream = getClass().getResourceAsStream(outputFileName);

        assert inputStream != null;
        assert expectedStream != null;

        try (
                BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedReader expectationsReader = new BufferedReader(new InputStreamReader(expectedStream))) {
            int arrayLength = Integer.parseInt(inReader.readLine().trim());

            List<Integer> q = Stream.of(inReader.readLine().replaceAll("\\s+$", "").split(" "))
                    .map(Integer::parseInt)
                    .collect(toList());

            var actual = NewYearChaos.minimumBribes(q);
            var expected =


                    expectationsReader.readLine();

            assertThat(actual).isEqualTo(expected) ;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
