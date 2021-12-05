package org.lecomte.hackerrank.medium;

import java.util.List;

public class NewYearChaos {

    // Computes the persons who have bribed the person at finalPos
    // It means they were behind and in the end they are before
    public static int bribesCount(List<Integer> q, int finalPos) {
        var bribesCount = 0;
        var initialPos = q.get(finalPos - 1);
        // We don't need to check the full queue before
        // Since you can't bribe more than 2 times, we only need to check
        // between initialPos - 1 and finalPos - 1
        for (var j = Math.max(1, initialPos - 1); j < finalPos; j++) {
            if (q.get(j - 1) > initialPos) {
                bribesCount = bribesCount + 1;
            }
        }
        return bribesCount;
    }

    public static int computeMinimumBribes(List<Integer> q) {
        var bribes = 0;

        for (var currentPos = 1; currentPos <= q.size(); currentPos++) {
            var initialPos = q.get(currentPos - 1);
            var posDiff = initialPos - currentPos;
            if (posDiff > 2) {
                return -1;
            }
            bribes = bribes + bribesCount(q, currentPos);
        }
        return bribes;
    }

    public static String minimumBribes(List<Integer> q) {
        var bribesCount = computeMinimumBribes(q);

        if (bribesCount >= 0) {
            return "" + bribesCount;
        }
        else {
            return "Too chaotic";
        }
    }
}
