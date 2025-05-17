package com.example.game

import javafx.scene.paint.Color
import javafx.scene.canvas.GraphicsContext
import com.example.util.Vector3

class Food(pos: Vector3, size: Vector3 = Vector3(1, 1, 0), color: Color = Color.RED) : GameObject(pos, size, color) {
    override fun draw(context: GraphicsContext, cellSize: Double) {
        context.fill = color
        val scaledSizeX = size.x * cellSize
        val scaledSizeY = size.y * cellSize
        val padding = cellSize * 0.1
        context.fillOval(
            pos.x * cellSize + padding, 
            pos.y * cellSize + padding, 
            scaledSizeX - padding * 2,
            scaledSizeY - padding * 2
        )
    }
}