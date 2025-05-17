package com.example.game

import kotlin.random.Random
import com.example.util.Vector3

class World(val size: Vector3) {
    var running: Boolean = false
    var paused: Boolean = false
    lateinit var snake: Snake
    lateinit var food: Food
    lateinit var walls: List<Wall>
    lateinit var playerName: String
    
    companion object {
        const val CELL_SIZE = 20.0
    }

    fun setup(playerName: String) {
        snake = Snake(size / 2) // Start the snake in the middle of the world
        walls = generateWalls()
        food = generateFood()
        this.playerName = playerName
    }

    fun start() {
        running = true
    }

    fun stop() {
        println("stop")
        running = false
    }

    fun togglePause() {
        paused = !paused
    }

    fun reset() {
        setup(playerName)
        running = false
    }

    fun tick() {
        println("running: ${running}, paused: ${paused}")
        if (!running || paused) {
            return
        }

        println("tick")

        snake.move()
        handleCollision()
    }

    fun handleCollision() {
        if (snake.collide(food)) {
            snake.growLength()
            food = generateFood()
            return
        }
        val selfCollision = snake.checkSelfCollision()
        val wallCollision = walls.any {snake.collide(it)}
        // println("selfcoll: $selfCollision")
        println("selfcoll: $selfCollision, wallcoll: $wallCollision")
        if (selfCollision || wallCollision) {
            stop()
        }
    }

    fun generateWalls(): List<Wall> {
        // Every corner wall is added twice but it doesn't matter.
        var wallPositions: MutableSet<Vector3> = mutableSetOf()
        for (x in 0 until size.x) {
            wallPositions.add(Vector3(x, 0, 1)) // Top
            wallPositions.add(Vector3(x, size.y - 1, 1)) // Bottom
        }
        for (y in 0 until size.y) {
            wallPositions.add(Vector3(0, y, 1)) // Left
            wallPositions.add(Vector3(size.x - 1, y, 1)) // Right
        }
        return wallPositions.map { Wall(it, Vector3(1, 1, 0)) }.toList()
    }

    fun generateFood(): Food {
        var foodPos: Vector3
        do {
            foodPos = Vector3(
                Random.nextInt(1, size.x - 1), // These bounds are selected so the food can never collide with the bounding walls
                Random.nextInt(1, size.y - 1),
                1
            )
        }
        while (snake.collide(foodPos)) // The food can never be placed where the snake already is
        return Food(foodPos)
    }
}