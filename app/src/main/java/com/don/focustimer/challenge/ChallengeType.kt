package com.don.focustimer.challenge

sealed class ChallengeType(
    val displayName: String,
    val isPremium: Boolean,
    val description: String
) {
    object Addition : ChallengeType(
        displayName = "Addition",
        isPremium = false,
        description = "Solve a 3-digit addition problem to dismiss the alarm"
    )

    object Multiplication : ChallengeType(
        displayName = "Multiplication",
        isPremium = true,
        description = "Solve a 3-digit multiplication problem to dismiss the alarm"
    )

    object PresetMessage : ChallengeType(
        displayName = "Preset Message",
        isPremium = false,
        description = "Acknowledge your custom motivational message"
    )

    object RandomMessage : ChallengeType(
        displayName = "Random Message",
        isPremium = false,
        description = "Acknowledge a random motivational quote"
    )

    object ScienceQuestion : ChallengeType(
        displayName = "Science Question",
        isPremium = true,
        description = "Answer a multiple choice science trivia question"
    )

    object Mindfulness : ChallengeType(
        displayName = "Mindfulness",
        isPremium = true,
        description = "Complete a calming mindfulness prompt by holding to dismiss"
    )

    companion object {
        fun all(): List<ChallengeType> = listOf(
            Addition, Multiplication, PresetMessage, RandomMessage, ScienceQuestion, Mindfulness
        )

        fun free(): List<ChallengeType> = all().filter { !it.isPremium }

        fun premium(): List<ChallengeType> = all().filter { it.isPremium }
    }
}
