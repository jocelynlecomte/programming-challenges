package org.lecomte.sort;

public class BubbleSort {

    // In bubble sort, we start at the beginning of the array and swap the first two elements if the first is greater
    // than the second.Then, we go to the next pair, and so on, continuously making sweeps of the array until it is
    // sorted. In doing so, the smaller items slowly "bubble" up to the beginning of the list.
    // This is the dumbest version, where we walk through the entire array in the inner loop
    public static void basicBubbleSort(int[] arr) {
        var n = arr.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (arr[j + 1] < arr[j]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    // The idea is the same, but we don't walk through the entire array 2 times
    // The scope of the inner loop is reduced by one each time since the last element
    // is at the right place
    public static void improvedBubbleSort(int[] arr) {
        var n = arr.length;
        for (int i = n - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j + 1] < arr[j]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        var temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
