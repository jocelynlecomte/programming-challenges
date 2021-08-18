package org.lecomte.hackerrank.medium;

public class MinimumSwaps2 {
    static int minimumSwaps(int[] arr) {
        int swaps = 0;
        boolean sorted = true;
        do {
            sorted = true;
            for (int i = 0; i < arr.length; i++) {
                if (misplaced(arr, i)) {
                    swap(arr, i, arr[i] - 1);
                    swaps++;
                    sorted = false;
                }
            }
        } while (!sorted);

        return swaps;
    }

    static boolean misplaced(int[] arr, int pos) {
        return arr[pos] != pos + 1;
    }

    static void swap(int[] arr, int pos1, int pos2) {
        int temp = arr[pos1];
        arr[pos1] = arr[pos2];
        arr[pos2] = temp;
    }
}
