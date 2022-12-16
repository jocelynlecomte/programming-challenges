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

    public Player getWinner(Player opponent) {
        int compare = this.sign.compare(opponent.sign);
        if (compare == 0) {
            return (this.number < opponent.number) ? this : opponent;
        } else {
            return (compare < 0) ? opponent : this;
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "number=" + number +
                ", sign=" + sign +
                '}';
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

class Round {
    List<Player> players;
    List<Player> winners;

    public Round(List<Player> players) {
        this.players = players;
        this.winners = getWinners();
    }

    public boolean hasOnlyOneWinner() {
        return winners.size() == 1;
    }

    private List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        for (int i = 0; i < players.size(); i = i + 2) {
            winners.add(players.get(i).getWinner(players.get(i + 1)));
        }
        return winners;
    }

    public Player getOpponent(Player player) {
        Player opponent = null;
        for (int i = 0; i < players.size() && opponent == null; i++) {
            if (players.get(i).equals(player)) {
                opponent = (i % 2 == 0) ? players.get(i + 1) : players.get(i - 1);
            }
        }
        return opponent;
    }
}

class Tournament {
    List<Player> players;
    List<Round> rounds;
    Player winner;
    List<Player> winnerOpponents;

    public Tournament(List<Player> players) {
        this.players = players;
        this.rounds = getSuccessiveRounds();
        this.winner = getWinner();
        this.winnerOpponents = getWinnerOpponents();
    }

    private Player getWinner() {
        return rounds.get(rounds.size() - 1).winners.get(0);
    }

    private List<Player> getWinnerOpponents() {
        return rounds.stream()
                .map(round -> round.getOpponent(winner))
                .collect(toList());
    }

    private List<Round> getSuccessiveRounds() {
        List<Round> rounds = new ArrayList<>();
        Round currentRound = new Round(players);

        while (!currentRound.hasOnlyOneWinner()) {
            rounds.add(currentRound);
            currentRound = new Round(currentRound.winners);
        }
        rounds.add(currentRound);

        return rounds;
    }
}

class Solver {
    private final List<Player> players;

    public Solver(List<InputLine> inputLines) {
        players = inputLines.stream().map(Player::new).collect(toList());
    }

    public List<OutputLine<Integer>> solve() {
        Tournament tournament = new Tournament(players);
        Player tournamentWinner = tournament.winner;
        List<Integer> opponentsNumbers = tournament.winnerOpponents.stream().map(player -> player.number).collect(toList());

        OutputLine<Integer> outputLine1 = new OutputLine<>(tournamentWinner.number);
        OutputLine<Integer> outputLine2 = new OutputLine<>(opponentsNumbers);
        return List.of(outputLine1, outputLine2);
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
