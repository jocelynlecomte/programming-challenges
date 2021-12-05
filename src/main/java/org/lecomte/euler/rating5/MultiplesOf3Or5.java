package org.lecomte.euler.rating5;

import java.util.stream.IntStream;

/**
 * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9. The sum of these multiples is 23.
 * <p>
 * Find the sum of all the multiples of 3 or 5 below 1000.
 */
public class MultiplesOf3Or5 {
    public int bruteForce(int limit) {
        return IntStream.rangeClosed(1, limit).filter(i -> i % 3 == 0 || i % 5 == 0).sum();
    }

    /**
     * Rationale:
     * The result is the sum of the 3's multiples + the sum of the 5's multiples
     * And we have to subtract the 15's multiples because they were taken into account 2 times
     */
    public int mathMethod(int limit) {
        return sumAllBaseMultiplesUnderLimit(limit, 3) + sumAllBaseMultiplesUnderLimit(limit, 5) - sumAllBaseMultiplesUnderLimit(limit, 15);
    }

    /**
     * Rationale
     * The sum of base multiples under limit is base times the sum of all the numbers under (limit / base)
     */
    private int sumAllBaseMultiplesUnderLimit(int limit, int base) {
        return base * sumAllTerms(limit / base);
    }

    private int sumAllTerms(int n) {
        return n * (n + 1) / 2;
    }
}
