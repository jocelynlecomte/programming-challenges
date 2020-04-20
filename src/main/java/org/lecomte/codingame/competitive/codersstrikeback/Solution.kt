package org.lecomte.codingame.competitive.codersstrikeback

import java.util.*
import kotlin.math.roundToInt

class Position(val x: Int, val y: Int)

class Input(
        val position: Position,
        val nextCheckpointPosition: Position,
        val nextCheckpointDist: Int,
        val nextCheckpointAngle: Int,
        val opponentPosition: Position)

class Bot {
    private val BOOST_DISTANCE_THRESHOLD = 5000;
    private val THRUST_BRAKE_DISTANCE_START = 2000;
    private val THRUST_BRAKE_DISTANCE_STOP = 1000;
    private val MAX_THRUST = 100;
    private val MIN_THRUST = 10; // TODO  à valider
    public val a: Double = (MAX_THRUST - MIN_THRUST * 1.0) / (THRUST_BRAKE_DISTANCE_START - THRUST_BRAKE_DISTANCE_STOP);
    public val b: Double = MAX_THRUST - THRUST_BRAKE_DISTANCE_START * a;
    private val MAP_CENTER = Position(8000, 4500);

    private var input: Input = Input(Position(0, 0), Position(0, 0), 0, 0, Position(0, 0));
    private var boostReady = true;

    fun refresh(input: Input): Unit {
        this.input = input;
    }

    fun computeThrust(): Int {
        val thrust: Int;

        if (input.nextCheckpointAngle > 90 || input.nextCheckpointAngle < -90) {
            thrust = 0;
        } else if (input.nextCheckpointDist < THRUST_BRAKE_DISTANCE_STOP) {
            thrust = MIN_THRUST;
        } else if (input.nextCheckpointDist > THRUST_BRAKE_DISTANCE_START) {
            thrust = MAX_THRUST;
        } else {
            thrust = ((a * input.nextCheckpointDist) + b).roundToInt();
        };

        return thrust;
    }

    fun computeThrustAsString(): String {
        System.err.println("DISTANCE -> ${input.nextCheckpointDist}");
        return if (input.nextCheckpointAngle < 10 && input.nextCheckpointAngle > -10 && input.nextCheckpointDist > BOOST_DISTANCE_THRESHOLD && boostReady) {
            boostReady = false;
            "BOOST";
        } else {
            computeThrust().toString();
        }
    }

    fun computeDestination(): Position {
        if (input.nextCheckpointDist < 2500) {
            return MAP_CENTER;
        } else {
            return input.nextCheckpointPosition;
        }
    }

    fun output(): String {
        val thrust = computeThrustAsString();
        val destination = computeDestination();
        return "${destination.x} ${destination.y} $thrust"
    }
}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val bot = Bot();

    // game loop
    while (true) {
        val position = Position(input.nextInt(), input.nextInt());
        val nextCheckpointPosition = Position(input.nextInt(), input.nextInt());
        val nextCheckpointDist = input.nextInt();
        val nextCheckpointAngle = input.nextInt();
        val opponentPosition = Position(input.nextInt(), input.nextInt());

        bot.refresh(Input(position, nextCheckpointPosition, nextCheckpointDist, nextCheckpointAngle, opponentPosition));
        // Write an action using println()
        // To debug: System.err.println("Debug messages...");

        // Edit this line to output the target position
        // and thrust (0 <= thrust <= 100)
        // i.e.: "x y thrust"
        println(bot.output());


    }
}

