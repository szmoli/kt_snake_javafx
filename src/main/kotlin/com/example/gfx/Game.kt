package com.example.gfx

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.control.Button
import javafx.scene.control.TextInputDialog
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.ButtonBar
import javafx.scene.layout.VBox
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.geometry.Insets
import com.example.game.World
import com.example.game.Snake
import com.example.util.Vector3
import java.io.File

class Game : Application() {
    companion object {
        const val WIDTH = 600
        const val HEIGHT = 600
        const val BASE_FRAME_TIME = 200_000_000L
        const val SPEED_MULTIPLIER = 2L
    }

    lateinit var gameLoop: AnimationTimer
    val leaderboardFile = File("leaderboard.dat")
    lateinit var stage: Stage
    val gameScene: Scene by lazy { initGameScene() }
    val menuScene: Scene by lazy { initMenuScene() }
    lateinit var leaderboardScene: Scene

    lateinit var playerName: String
    var frameTime = Game.BASE_FRAME_TIME
    var spacePressed = false
    val world: World = World(Vector3(30, 30, 0))
    var lastUpdate: Long = 0L
    lateinit var pendingDir: Vector3
    lateinit var graphicsContext: GraphicsContext

    fun showGameOverDialog() {
        Platform.runLater {
            val dialog = Dialog<ButtonType>().apply {
                title = "Game Over"
                headerText = """
                    Game Over!
                    Player: $playerName
                    Final Score: ${world.snake.body.size}
                """.trimIndent()
                dialogPane.buttonTypes.addAll(
                    ButtonType("Play Again", ButtonBar.ButtonData.OK_DONE),
                    ButtonType("Main Menu", ButtonBar.ButtonData.CANCEL_CLOSE)
                )
            }

            val result = dialog.showAndWait()
            result.ifPresent { 
                when (it.buttonData) {
                    ButtonBar.ButtonData.OK_DONE -> initGame()
                    else -> stage.scene = menuScene
                }
            }
        }
    }

    fun showPlayerNameDialog() {
        val dialog = TextInputDialog().apply {
            title = "Player Name"
            headerText = "Enter your name"
            contentText = "Name:"
            dialogPane.style = "-fx-font-size: 14pt;"
            editor.text = ""
            dialogPane.buttonTypes.setAll(ButtonType.OK, ButtonType.CANCEL)
        }
    
        val result = dialog.showAndWait()
    
        result.ifPresentOrElse(
            { name ->
                playerName = name.trim().takeIf { it.isNotEmpty() } ?: "Player"
                initGame()
                stage.scene = gameScene
            },
            {
                stage.scene = menuScene
            }
        )
    }

    fun initMenuScene(): Scene {
        val startButton = Button("Start Game").apply {
            style = "-fx-font-size: 20;"
            setOnAction {
                showPlayerNameDialog()
            }
        }

        val leaderboardButton = Button("Leaderboard").apply {
            style = "-fx-font-size: 20;"
            // setOnAction { showLeaderboardScene() }
        }

        val exitButton = Button("Exit").apply {
            style = "-fx-font-size: 20;"
            setOnAction { Platform.exit() }
        }

        val layout = VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(startButton, leaderboardButton, exitButton)
            padding = Insets(20.0)
        }

        return Scene(layout, 600.0, 600.0)
    }

    fun initGameScene(): Scene {
        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())

        val root = Pane().apply {
            children.add(canvas)
        }

        return Scene(root, 600.0, 600.0).apply {
            setOnKeyPressed { event ->
                when (event.code) {
                    KeyCode.W -> pendingDir = Vector3(0, -1, 0)
                    KeyCode.S -> pendingDir = Vector3(0, 1, 0)
                    KeyCode.A -> pendingDir = Vector3(-1, 0, 0)
                    KeyCode.D -> pendingDir = Vector3(1, 0, 0)
                    KeyCode.P -> world.togglePause()
                    KeyCode.SPACE -> {
                        if (!spacePressed) {
                            spacePressed = true
                            frameTime = (BASE_FRAME_TIME / SPEED_MULTIPLIER)
                        }
                    }
                    else -> {}
                }
            }

            setOnKeyReleased { event ->
                when (event.code) {
                    KeyCode.SPACE -> {
                        if (spacePressed) {
                            spacePressed = false
                            frameTime = BASE_FRAME_TIME
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun initGame() {
        world.setup(playerName)
        pendingDir = world.snake.dir
        world.start()
        lastUpdate = System.nanoTime()
        val canvas = (gameScene.root as Pane).children[0] as Canvas
        graphicsContext = canvas.getGraphicsContext2D()

        // Main loop
        gameLoop = object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                val now = System.nanoTime()

                if (!world.running) {
                    gameLoop.stop()
                    saveScore()
                    showGameOverDialog()
                }

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
                    graphicsContext.fillText("Player: ${playerName}\tPoints: ${world.snake.body.size}", 3.0, HEIGHT.toDouble() - 3.0) // TODO: remove magic constants

                    lastUpdate = now
                }
            }
        }.also {it.start()}
    }

    fun saveScore() {
        leaderboardFile.appendText("${world.playerName},${world.snake.body.size}\n")
    }

    override fun start(mainStage: Stage) {        
        stage = mainStage.apply {
            title = "Snake"
            scene = menuScene
            show()
        }
    }
}
