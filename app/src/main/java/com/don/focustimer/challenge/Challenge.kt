package com.don.focustimer.challenge

sealed class Challenge {
    data class MathChallenge(
        val num1: Int,
        val num2: Int,
        val operation: String,
        val correctAnswer: Int
    ) : Challenge()

    data class MessageChallenge(
        val message: String,
        val requiresHold: Boolean = false,
        val holdDurationMs: Long = 0
    ) : Challenge()

    data class QuizChallenge(
        val question: String,
        val options: List<String>,
        val correctIndex: Int
    ) : Challenge()
}
