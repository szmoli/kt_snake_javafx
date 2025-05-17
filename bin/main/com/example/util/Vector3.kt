package com.example.util

data class Vector3(val x: Int, val y: Int, val w: Int) {
    operator fun times(scalar: Int): Vector3 {
        return Vector3(
            x * scalar,
            y * scalar,
            w * scalar
        )
    }

    operator fun div(n: Int): Vector3 {
        return Vector3(
            x / n,
            y / n,
            w / n
        )
    }

    operator fun plus(v: Vector3): Vector3 {
        return Vector3(
            x + v.x,
            y + v.y,
            w + v.w
        )
    }

    /**
     * Calculates the vector pointing from this to vector (i.e. result = vector - this)
     * @param vector Where
     */
    operator fun minus(v: Vector3): Vector3 {
        return this + (v * -1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector3) return false
        
        return this.x == other.x && this.y == other.y && this.w == other.w
    }
}
