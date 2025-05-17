package com.example.game

import javafx.scene.paint.Color
import javafx.scene.canvas.GraphicsContext
import com.example.util.Vector3

class Snake(pos: Vector3, size: Vector3 = Vector3(1, 1, 0), color: Color = Color.GREEN) : GameObject(pos, size, color) {
    var body: MutableList<Vector3> = mutableListOf<Vector3>(pos)
    var dir: Vector3 = Vector3(1, 0, 0)

    /**
     * Moves the snake in the current direction.
     */
    fun move() {
        // Move body parts from the back to the front.
        for (i in body.size - 1 downTo 1) {
            val previous: Vector3 = body[i - 1]
            body[i] = previous
        }

        // Move the head in the current direction.
        body[0] = body[0] + dir
    }

    /**
     * Grows the snake's length by one.
     */
    fun growLength() {
        val last = body.last()
        val offset: Vector3 = if (body.size == 1) {
            // Use the inverse of the direction as the offset.
            Vector3(dir.x, dir.y, dir.w)
        }
        else {
            // Calculate the offset based on the last two body pieces.
            val secondLast = body[body.size - 2]
            secondLast - last
        }

        val inverseOffset = offset * -1
        var newPiece = last + inverseOffset
        body.add(newPiece)
    }

    /**
     * Changes the direction of the snake.
     * @param newDir The new direction
     */
    fun changeDirection(newDir: Vector3) {
        if (newDir == dir * -1) return // Don't allow 180 degree turns

        dir = newDir
    }

    override fun collide(other: GameObject): Boolean {
        for (bodyPart in body) {
            if (other.collide(bodyPart)) return true
        }

        return false
    }

    fun checkSelfCollision(): Boolean {
        val head = body.first()
        return body.drop(1).any {bodyPart -> head == bodyPart}
    }

    override fun draw(context: GraphicsContext, cellSize: Double) {
        context.fill = color
        val scaledSizeX = size.x * cellSize
        val scaledSizeY = size.y * cellSize
        for (bodyPart in body) {
            context.fillOval(
                bodyPart.x * cellSize, 
                bodyPart.y * cellSize, 
                scaledSizeX,
                scaledSizeY
            )
        }
    }
}