package com.example.interfaces

import javafx.scene.canvas.GraphicsContext

interface IDrawable {
    fun draw(context: GraphicsContext, cellSize: Double)
}