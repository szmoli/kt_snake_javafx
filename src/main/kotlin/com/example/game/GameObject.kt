package com.example.game

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import com.example.util.Vector3
import com.example.interfaces.IDrawable
import com.example.interfaces.IObject

abstract class GameObject(val pos: Vector3, val size: Vector3, val color: Color) : IObject, IDrawable {
    // TODO: Check if this works correctly
    override fun collide(other: GameObject): Boolean {
        val thisLeft = pos.x
        val thisRight = pos.x + size.x
        val thisTop = pos.y
        val thisBottom = pos.y + size.y
    
        val otherLeft = other.pos.x
        val otherRight = other.pos.x + other.size.x
        val otherTop = other.pos.y
        val otherBottom = other.pos.y + other.size.y
    
        return (thisLeft < otherRight && 
                thisRight > otherLeft && 
                thisTop < otherBottom && 
                thisBottom > otherTop)
    }

    override fun collide(pos: Vector3): Boolean {
        val collisionBox = this.pos + size
        val xCollision = pos.x >= this.pos.x && pos.x < collisionBox.x
        val yCollision = pos.y >= this.pos.y && pos.y < collisionBox.y        
        return xCollision && yCollision
    }

    override fun draw(context: GraphicsContext, cellSize: Double) {
        val scaledSizeX = size.x * cellSize
        val scaledSizeY = size.y * cellSize
        context.fill = color
        context.fillRect(
            pos.x * cellSize,
            pos.y * cellSize,
            scaledSizeX,
            scaledSizeY
        )
    }
}