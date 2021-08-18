package org.lecomte.sort;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SorterTest {

    @Nested
    class BasicBubbleSort {
        @Test
        void testBubbleSort1() {
            int[] arr = {5 , 1, 4, 2, 8};
            BubbleSort.basicBubbleSort(arr);

            assertThat(arr).isSorted();
        }

        @Test
        void testBubbleSort2() {
            int[] arr = {5, 4, 3, 2, 1};
            BubbleSort.basicBubbleSort(arr);

            assertThat(arr).isSorted();
        }
    }

    @Nested
    class ImprovedBubbleSort {
        @Test
        void testBubbleSort1() {
            int[] arr = {5 , 1, 4, 2, 8};
            BubbleSort.improvedBubbleSort(arr);

            assertThat(arr).isSorted();
        }

        @Test
        void testBubbleSort2() {
            int[] arr = {5, 4, 3, 2, 1};
            BubbleSort.improvedBubbleSort(arr);

            assertThat(arr).isSorted();
        }
    }
}
