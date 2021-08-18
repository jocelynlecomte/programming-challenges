package org.lecomte.hackerrank.medium;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class MinimumSwaps2Test {

    @Test
    void minimumSwaps00() {
        filesAssert("MinimumSwaps2Input00.txt", "MinimumSwaps2Output00.txt");
    }

    private void filesAssert(String inputFileName, String outputFileName) {
        InputStream inputStream = getClass().getResourceAsStream(inputFileName);
        InputStream expectedStream = getClass().getResourceAsStream(outputFileName);

        assert inputStream != null;
        assert expectedStream != null;

        try (
                BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedReader expectationsReader = new BufferedReader(new InputStreamReader(expectedStream));
                Scanner inScanner = new Scanner(inReader);
                Scanner expectationsScanner = new Scanner(expectationsReader)
        ) {
            int n = inScanner.nextInt();
            inScanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            int[] arr = new int[n];

            String[] arrItems = inScanner.nextLine().split(" ");
            inScanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int i = 0; i < n; i++) {
                int arrItem = Integer.parseInt(arrItems[i]);
                arr[i] = arrItem;
            }

            var actual = MinimumSwaps2.minimumSwaps(arr);
            var expected = expectationsScanner.nextInt();

            assertThat(actual).isEqualTo(expected) ;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
