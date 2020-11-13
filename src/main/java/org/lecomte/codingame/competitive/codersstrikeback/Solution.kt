package org.lecomte.codingame.competitive.codersstrikeback

import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

// CONSTANTS
const val MAX_THRUST = 100
const val CHECKPOINT_RADIUS = 600
const val MAX_X = 16000
const val MAX_Y = 9000

// PARAMETERS
const val MIN_THRUST = 30;

data class Point2D(val x: Int, val y: Int) {
    fun move(vector: Vector2D): Point2D {
        return Point2D(x + vector.dx, y + vector.dy)
    }

    fun isInRadius(other: Point2D, radius: Int): Boolean {
        return abs((x - other.x) * (y - other.y)) < radius * radius;
    }
}

data class Vector2D(val dx: Int, val dy: Int) {
    fun squareLength(): Int {
        return dx * dx + dy * dy
    }
}

class Checkpoints {
    private val mapCenter = Point2D(MAX_X / 2, MAX_Y / 2)

    private val list = mutableListOf<Point2D>()
    private var current = Point2D(0, 0)
    private var complete = false

    fun contains(checkpoint: Point2D) = list.contains(checkpoint)

    fun add(checkpoint: Point2D) {
        if (!list.contains(checkpoint)) {
            list.add(checkpoint)
        } else {
            complete = true
        }
    }

    fun afterNextOrCenter(): Point2D {
        if (!complete) {
            return mapCenter
        }

        for (i in 0 until list.size) {
            if (current == list[i]) {
                val nextIndex = if (i == list.size - 1) 0 else i + 1
                return list[nextIndex]
            }
        }
        return mapCenter
    }

    fun isComplete() = complete

    fun setCurrent(checkpoint: Point2D) {
        current = checkpoint
    }

    override fun toString(): String {
        return "Checkpoints(size=${list.size}, list=$list)"
    }
}

data class Input(
        val position: Point2D,
        val nextCheckpointPosition: Point2D,
        val nextCheckpointDist: Int,
        val nextCheckpointAngle: Int,
        val opponentPosition: Point2D)


abstract class ThrustComputer {
    private val boostDistanceThreshold = 5000

    fun computeThrust(input: Input): Int {
        return if (shouldBoost(input)) {
            200
        } else if (input.nextCheckpointAngle > 90 || input.nextCheckpointAngle < -90) {
            0
        } else {
            specificCompute(input)
        }
    }

    abstract fun specificCompute(input: Input): Int

    private fun shouldBoost(input: Input): Boolean {
        return abs(input.nextCheckpointAngle) < 10 && input.nextCheckpointDist > boostDistanceThreshold
    }
}

class LinearThrustComputer : ThrustComputer() {
    private val brakeDistanceStart = 2000
    private val brakeDistanceStop = 1000
    private val a = (MAX_THRUST - MIN_THRUST * 1.0) / (brakeDistanceStart - brakeDistanceStop)
    private val b = MAX_THRUST - brakeDistanceStart * a

    override fun specificCompute(input: Input): Int {
        return when {
            input.nextCheckpointDist < brakeDistanceStop -> {
                MIN_THRUST
            }
            input.nextCheckpointDist > brakeDistanceStart -> {
                MAX_THRUST
            }
            else -> {
                ((a * input.nextCheckpointDist) + b).roundToInt()
            }
        }
    }
}

class StepThrustComputer : ThrustComputer() {
    private val approachThrustCoeff = (MAX_THRUST * 0.8).roundToInt()

    override fun specificCompute(input: Input): Int {
        return when {
            input.nextCheckpointDist < CHECKPOINT_RADIUS -> {
                0
            }
            input.nextCheckpointDist > 2 * CHECKPOINT_RADIUS -> {
                MAX_THRUST
            }
            else -> {
                approachThrustCoeff
            }
        }
    }
}

class Pod(private val thrustComputer: ThrustComputer) {

    private var turn = 0;
    private var previousPosition = Point2D(0, 0)
    private var previousCheckpoint = Point2D(0, 0)
    private var speed = Vector2D(0, 0)
    private val checkpoints = Checkpoints()
    private var input = Input(previousPosition, previousCheckpoint, 0, 0, Point2D(0, 0))

    fun startTurn(input: Input) {
        if (previousCheckpoint != input.nextCheckpointPosition && !checkpoints.isComplete()) {
            checkpoints.add(input.nextCheckpointPosition)
        }

        if (turn > 0) {
            speed = Vector2D(input.position.x - previousPosition.x, input.position.y - previousPosition.y)
        }
        turn++
        checkpoints.setCurrent(input.nextCheckpointPosition)

        this.input = input
    }

    fun endTurn() {
        previousPosition = input.position
        previousCheckpoint = input.nextCheckpointPosition
        System.err.println("TURN --> $turn, DISTANCE --> ${input.nextCheckpointDist}, SPEED --> $speed")
    }

    private fun computeDestination(): Point2D {
        return if (shouldPointToNextCheckpoint()) {
            checkpoints.afterNextOrCenter()
        } else {
            input.nextCheckpointPosition
        }
    }

    private fun shouldPointToNextCheckpoint(): Boolean {
        return input.nextCheckpointDist < CHECKPOINT_RADIUS && speed.squareLength() > 40000
    }

    fun output(): String {
        val thrust = thrustComputer.computeThrust(input)
        val command = if (thrust > MAX_THRUST) "BOOST" else thrust.toString()
        val destination = computeDestination()
        return "${destination.x} ${destination.y} $command"
    }
}

fun main() {
    val input = Scanner(System.`in`)
    val bot = Pod(StepThrustComputer())

    // game loop
    while (true) {
        val position = Point2D(input.nextInt(), input.nextInt())
        val nextCheckpointPosition = Point2D(input.nextInt(), input.nextInt())
        val nextCheckpointDist = input.nextInt()
        val nextCheckpointAngle = input.nextInt()
        val opponentPosition = Point2D(input.nextInt(), input.nextInt())

        bot.startTurn(Input(position, nextCheckpointPosition, nextCheckpointDist, nextCheckpointAngle, opponentPosition))
        println(bot.output())
        bot.endTurn()
    }
}

