package com.example.gfx

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import com.example.game.World
import com.example.game.Snake
import com.example.util.Vector3

class Game : Application() {
    val world: World = World(Vector3(30, 30, 0))
    var frameTime = 200_000_000L
    var spacePressed = false
    lateinit var pendingDir: Vector3
    
    companion object {
        const val WIDTH = 600
        const val HEIGHT = 600
    }
    
    lateinit var mainScene: Scene
    // lateinit var leaderboardScene: Scene
    // lateinit var menuScene: Scene
    lateinit var graphicsContext: GraphicsContext

    var lastUpdate: Long = 0L
    val activeKeys = mutableSetOf<KeyCode>()

    override fun start(mainStage: Stage) {
        mainStage.title = "Snake"

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        root.children.add(canvas)
        
        // Setup
        graphicsContext = canvas.graphicsContext2D
        setupInputHandling()
        world.setup()
        pendingDir = world.snake.dir
        world.start()

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                val now = System.nanoTime()
                if (now - lastUpdate >= frameTime) {
                    // Update
                    world.snake.changeDirection(pendingDir)
                    world.tick()
    
                    // Draw
                    graphicsContext.fill = Color.BEIGE
                    graphicsContext.fillRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())
                    world.snake.draw(graphicsContext, World.CELL_SIZE)
                    world.food.draw(graphicsContext, World.CELL_SIZE)
                    for (wall in world.walls) wall.draw(graphicsContext, World.CELL_SIZE)
                    graphicsContext.fill = Color.BLACK
                    graphicsContext.fillText("Points: ${world.snake.body.size}", 3.0, HEIGHT.toDouble() - 3.0) // TODO: remove magic constants

                    lastUpdate = now
                }
            }
        }.start()

        mainStage.show()
    }

    private fun setupInputHandling() {
        mainScene.setOnKeyPressed { event ->
            when (event.code) {
                KeyCode.W -> pendingDir = Vector3(0, -1, 0)
                KeyCode.S -> pendingDir = Vector3(0, 1, 0)
                KeyCode.A -> pendingDir = Vector3(-1, 0, 0)
                KeyCode.D -> pendingDir = Vector3(1, 0, 0)
                KeyCode.P -> world.togglePause()
                KeyCode.SPACE -> {
                    if (!spacePressed) {
                        spacePressed = true
                        frameTime /= 2
                    }
                }
                else -> {}
            }
        }

        mainScene.setOnKeyReleased { event ->
            when (event.code) {
                KeyCode.SPACE -> {
                    if (spacePressed) {
                        spacePressed = false
                        frameTime *= 2
                    }
                }
                else -> {}
            }
        }
    }
}
