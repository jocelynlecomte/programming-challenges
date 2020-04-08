package org.lecomte.codingame.easy.rpcls;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class InputLine {
    public final String playerNumber;
    public final String playerSign;

    public InputLine(String playerNumber, String playerSign) {
        this.playerNumber = playerNumber;
        this.playerSign = playerSign;
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

enum Sign {
    R {
        @Override
        public int compare(Sign other) {
            if (this == other) {
                return 0;
            } else if (other == P || other == S) {
                return -1;
            } else {
                return 1;
            }
        }
    },
    P {
        @Override
        public int compare(Sign other) {
            if (this == other) {
                return 0;
            } else if (other == C || other == L) {
                return -1;
            } else {
                return 1;
            }
        }
    },
    C {
        @Override
        public int compare(Sign other) {
            if (this == other) {
                return 0;
            } else if (other == R || other == S) {
                return -1;
            } else {
                return 1;
            }
        }
    },
    L {
        @Override
        public int compare(Sign other) {
            if (this == other) {
                return 0;
            } else if (other == R || other == C) {
                return -1;
            } else {
                return 1;
            }
        }
    },
    S {
        @Override
        public int compare(Sign other) {
            if (this == other) {
                return 0;
            } else if (other == P || other == L) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    public abstract int compare(Sign other);
}

class Player {
    public final int number;
    public final Sign sign;


    public Player(InputLine inputLine) {
        this.number = Integer.parseInt(inputLine.playerNumber);
        this.sign = Sign.valueOf(inputLine.playerSign);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return number == player.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}

class Solver {
    private List<Player> players;

    public Solver(List<InputLine> inputlines) {
        players = inputlines.stream().map(Player::new).collect(toList());
    }

    public List<OutputLine<Integer>> solve() {
        List<List<Player>> rounds = new ArrayList<>();
        List<Player> currentRound = players;

        while (currentRound.size() > 1) {
            rounds.add(currentRound);
            List<Player> nextTurn = new ArrayList<>();
            for (int i = 0; i < currentRound.size(); i = i + 2) {
                nextTurn.add(winner(currentRound.get(i), currentRound.get(i + 1)));
            }
            currentRound = nextTurn;
        }

        Player winner = currentRound.get(0);
        List<Integer> opponents = getOpponents(rounds, winner).stream().map(player -> player.number).collect(toList());

        OutputLine<Integer> outputLine1 = new OutputLine<>(winner.number);
        OutputLine<Integer> outputLine2 = new OutputLine<>(opponents);
        return List.of(outputLine1, outputLine2);
    }

    private Player winner(Player p1, Player p2) {
        int compare = p1.sign.compare(p2.sign);
        if (compare == 0) {
            return (p1.number < p2.number) ? p1 : p2;
        } else {
            return (compare < 0) ? p2 : p1;
        }
    }

    private List<Player> getOpponents(List<List<Player>> turns, Player winner) {
        return turns.stream()
                .map(players -> {
                    Player opponent = null;
                    for (int i = 0; i < players.size() && opponent == null; i++) {
                        if (players.get(i).equals(winner)) {
                            opponent = (i % 2 == 0) ? players.get(i + 1) : players.get(i - 1);
                        }
                    }
                    return opponent;
                }).collect(toList());
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
            String playerNumber = in.next();
            String playerSign = in.next();

            inputLines.add(new InputLine(playerNumber, playerSign));
        }
        return inputLines;
    }
}