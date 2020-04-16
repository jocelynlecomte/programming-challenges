package org.lecomte.codingame.medium.aneo;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

class Light {
    public final int distance;
    public final int duration;
    public final int cycleDuration;

    public Light(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
        this.cycleDuration = 2 * duration;
    }
}

class Input {
    public final int maxSpeed;
    public final int lightCount;
    public final List<Light> lights;

    public Input(int maxSpeed, int lightCount, List<Light> lights) {
        this.maxSpeed = maxSpeed * 1000;
        this.lightCount = lightCount;
        this.lights = lights;
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
    private final Input input;

    public Solver(Input input) {
        this.input = input;
    }

    public List<OutputLine<Integer>> solve() {
        List<OutputLine<Integer>> result = new ArrayList<>();

        boolean canPassAllLights = false;
        int currentSpeed = input.maxSpeed;
        while (!canPassAllLights && currentSpeed > 0) {
            Optional<Light> stoppingLight = input.lights.parallelStream()
                    .filter(canPass(currentSpeed))
                    .findFirst();
            if (stoppingLight.isEmpty()) {
                canPassAllLights = true;
            } else {
                currentSpeed = currentSpeed - 1000;
            }
        }

        result.add(new OutputLine<>(currentSpeed / 1000));
        return result;
    }

    private Predicate<Light> canPass(int speed) {
        return light -> {
            double passageTime = (light.distance * 3600.0) / speed;
            return !canPass(passageTime, light);
        };
    }

    private boolean canPass(double passageTime, Light light) {
        while (passageTime >= light.cycleDuration) {
            passageTime = passageTime - light.cycleDuration;
        }
        return passageTime < light.duration;
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
        Input input = parseInput(is);
        Solver solver = new Solver(input);

        return solver.solve();
    }

    public static Input parseInput(InputStream is) {
        Scanner in = new Scanner(is);
        int maxSpeed = in.nextInt();
        int lightCount = in.nextInt();

        List<Light> lights = IntStream.range(0, lightCount)
                .mapToObj(i -> {
                    int distance = in.nextInt();
                    int duration = in.nextInt();

                    return new Light(distance, duration);
                })
                .collect(Collectors.toList());

        return new Input(maxSpeed, lightCount, lights);
    }
}