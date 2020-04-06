package org.lecomte.codingame.easy.onedspreadsheet;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class InputLine {
    public final String operation;
    public final String arg1;
    public final String arg2;

    InputLine(String operation, String arg1, String arg2) {
        this.operation = operation;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
}

class OutputLine<T> {
    public final List<T> values;

    OutputLine(T value) {
        this.values = List.of(value);
    }

    OutputLine(List<T> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return values.stream()
                .map(String::valueOf)
                .collect(joining(" ", "", ""));
    }
}

enum Operation {
    VALUE {
        public Integer operate(Integer arg1, Integer arg2) {
            return arg1;
        }
    },
    ADD {
        public Integer operate(Integer arg1, Integer arg2) {
            return arg1 + arg2;
        }
    },
    SUB {
        public Integer operate(Integer arg1, Integer arg2) {
            return arg1 - arg2;
        }
    },
    MULT {
        public Integer operate(Integer arg1, Integer arg2) {
            return arg1 * arg2;
        }
    };

    public abstract Integer operate(Integer arg1, Integer arg2);
}

class Solver {
    private static Pattern REFERENCE_PATTERN = Pattern.compile("\\$([0-9]+)");
    private static String EMPTY_VALUE = "_";

    private Integer[] cells;
    private List<InputLine> inputs;

    public Solver(List<InputLine> inputlines) {
        cells = new Integer[inputlines.size()];
        this.inputs = inputlines;
    }

    public boolean isComputable(String arg) {
        if (arg.equals(EMPTY_VALUE)) {
            return true;
        }

        Matcher referenceMatcher = REFERENCE_PATTERN.matcher(arg);
        if (referenceMatcher.find()) {
            int cellNumber = Integer.parseInt(referenceMatcher.group(1));
            return cells[cellNumber] != null;
        } else {
            return true;
        }
    }

    public Integer getValue(String arg) {
        if (arg.equals(EMPTY_VALUE)) {
            return null;
        }

        Matcher referenceMatcher = REFERENCE_PATTERN.matcher(arg);
        if (referenceMatcher.find()) {
            int cellNumber = Integer.parseInt(referenceMatcher.group(1));
            return cells[cellNumber];
        } else {
            return Integer.valueOf(arg);
        }
    }

    public List<OutputLine<Integer>> solve() {
        boolean complete = false;
        while (!complete) {
            complete = true;
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) continue;
                InputLine inputLine = inputs.get(i);

                Operation operation = Operation.valueOf(inputLine.operation);

                if (operation == Operation.VALUE || (isComputable(inputLine.arg1) && isComputable(inputLine.arg2))) {
                    Integer arg1 = getValue(inputLine.arg1);
                    Integer arg2 = getValue(inputLine.arg2);
                    cells[i] = operation.operate(arg1, arg2);
                } else {
                    complete = false;
                }
            }
        }

        return Stream.of(cells).map(OutputLine::new).collect(toList());
    }

}

public class Solution {
    public static void main(String args[]) {
        main(args, System.in, System.out);
    }

    public static void main(String args[], InputStream is, PrintStream ps) {
        List<OutputLine<Integer>> result = solve(is);
        result.forEach(ps::println);
    }

    public static List<OutputLine<Integer>> solve(InputStream is) {
        List<InputLine> inputLines = parseInput(is);
        Solver solver = new Solver(inputLines);

        return solver.solve();
    }

    public static List<InputLine> parseInput(InputStream is) {
        Scanner in = new Scanner(is);
        int N = in.nextInt();
        List<InputLine> inputLines = new ArrayList<>(N);

        for (int i = 0; i < N; i++) {
            String operation = in.next();
            String arg1 = in.next();
            String arg2 = in.next();

            inputLines.add(new InputLine(operation, arg1, arg2));
        }
        return inputLines;
    }
}