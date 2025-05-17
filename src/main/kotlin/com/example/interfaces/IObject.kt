package com.example.interfaces

import com.example.util.Vector3
import com.example.game.GameObject

interface IObject {
    fun collide(other: GameObject): Boolean
    fun collide(pos: Vector3): Boolean
}