package org.lecomte.codingame.easy.ghostlegs;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.stream.Collectors.joining;

class Input {
    public final String[] topLabels;
    public final String[] bottomLabels;
    public final List<String> connections;

    public Input(String[] topLabels, String[] bottomLabels, List<String> connections) {
        this.topLabels = topLabels;
        this.bottomLabels = bottomLabels;
        this.connections = connections;
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

class Solver {
    private String[] topLabels;
    private String[] bottomLabels;
    private List<String> connections;

    public Solver(Input input) {
        topLabels = input.topLabels;
        bottomLabels = input.bottomLabels;
        connections = input.connections;
    }

    public List<OutputLine<String>> solve() {
        List<OutputLine<String>> result = new ArrayList<>();
        for (String topLabel : topLabels) {
            String currentTopLabel = topLabel;
            for (String connection : connections) {
                String matchingLabel = parse(currentTopLabel, connection);
                currentTopLabel = (matchingLabel != null) ? matchingLabel : currentTopLabel;
            }
            result.add(new OutputLine<>(topLabel + bottomLabels[topLabelIndex(currentTopLabel)]));
        }

        return result;
    }

    private String parse(String label, String connection) {
        if (connection.substring(0, 1).equals(label)) {
            return connection.substring(1, 2);
        } else if (connection.substring(1, 2).equals(label)) {
            return connection.substring(0, 1);
        } else {
            return null;
        }
    }

    private int topLabelIndex(String label) {
        for (int i = 0; i < topLabels.length; i++) {
            if (topLabels[i].equals(label)) {
                return i;
            }
        }
        return -1;
    }
}

public class Solution {
    public static void main(String args[]) {
        main(args, System.in, System.out);
    }

    public static void main(String args[], InputStream is, PrintStream ps) {
        List<OutputLine<String>> result = solve(is);
        result.forEach(ps::println);
    }

    public static List<OutputLine<String>> solve(InputStream is) {
        Input input = parseInput(is);
        Solver solver = new Solver(input);

        return solver.solve();
    }

    public static Input parseInput(InputStream is) {
        Scanner in = new Scanner(is);
        int W = in.nextInt();
        int H = in.nextInt();

        if (in.hasNextLine()) {
            in.nextLine();
        }

        String[] topLabels = null;
        String[] bottomLabels = null;
        List<String> connections = new ArrayList<>();

        for (int i = 0; i < H; i++) {
            String line = in.nextLine();
            if (i == 0) {
                topLabels = line.split("  ");
            } else if (i == H - 1) {
                bottomLabels = line.split("  ");
            } else {
                for (int j = 0; j < topLabels.length - 1; j++) {
                    int testCharPos = (j * 3) + 1;
                    if (line.substring(testCharPos, testCharPos + 1).equals("-")) {
                        connections.add(topLabels[j] + topLabels[j + 1]);
                    }
                }
            }
        }

        return new Input(topLabels, bottomLabels, connections);
    }
}