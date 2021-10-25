package org.lecomte.codingame.easy.sumspiraldiagonals;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Computes the sum of the numbers on the 2 diagonals
 * for a matrix arranged in spiral
 */
class Solver {
    private final int n;

    public Solver(int n) {
        this.n = n;
    }

    public long solve() {
        return this.solve(1, n);
    }

    /**
     * recursively computes the result by computing the values of the 4 corners for the given matrix
     * and calling itself for the inside matrix
     *
     * @param startingValue value of the upper-left corner
     * @param matrixSize
     */
    public long solve(int startingValue, int matrixSize) {
        if (matrixSize == 0) {
            return 0;
        } else if (matrixSize == 1) {
            return startingValue;
        } else {
            // Upper left: S
            // Upper right: S + matrixSize - 1
            // Lower right: S + 2 (matrixSize - 1)
            // Lower left: S + 3 (matrixSize - 1)
            // Starting value of the inside matrix: S + 4 (matrixSize - 1)
            // Size of the inside matrix: matrixSize - 2
            return 4L * startingValue + 6L * (matrixSize - 1) + solve(startingValue + 4 * (matrixSize - 1), matrixSize - 2);
        }
    }
}

public class Solution {

    public static void main(String args[]) {
        main(args, System.in, System.out);
    }

    public static void main(String args[], InputStream is, PrintStream ps) {
        long result = solve(is);
        ps.println(result);
    }

    public static long solve(InputStream is) {
        int input = parseInput(is);
        Solver solver = new Solver(input);

        return solver.solve();
    }

    public static int parseInput(InputStream is) {
        Scanner in = new Scanner(is);
        return in.nextInt();
    }
}
