package com.example.util

import java.io.File

object Leaderboard {
    data class Score(val playerName: String, val score: Int)

    private val leaderboard: MutableList<Score> = mutableListOf()
    private val leaderboardFile = File("leaderboard.dat")
    
    init {
        loadLeaderboard()
    }

    fun addToLeaderboard(score: Score) {
        leaderboard.add(score)
        leaderboard.sortByDescending { it.score }
        leaderboardFile.appendText("${score.playerName},${score.score}\n")
    }

    fun loadLeaderboard() {
        if (leaderboardFile.exists()) {
            leaderboard.clear()
            leaderboardFile.readLines().forEach { line ->
                line.split(",").takeIf { it.size == 2 }?.let {
                    val name = it[0]
                    val score = it[1].toIntOrNull()
                    score?.let { s -> leaderboard.add(Score(name, s)) }
                }
            }
            leaderboard.sortByDescending { it.score }
        }
    }

    fun getTopScores(limit: Int = 10): List<Score> {
        return leaderboard.take(limit)
    }
}