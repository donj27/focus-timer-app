package com.don.focustimer.challenge

import com.don.focustimer.data.QuoteBank
import com.don.focustimer.data.ScienceBank
import kotlin.random.Random

object ChallengeGenerator {

    fun generate(type: ChallengeType, userMessage: String? = null): Challenge {
        return when (type) {
            is ChallengeType.Addition -> generateAddition()
            is ChallengeType.Multiplication -> generateMultiplication()
            is ChallengeType.PresetMessage -> generatePresetMessage(userMessage)
            is ChallengeType.RandomMessage -> generateRandomMessage()
            is ChallengeType.ScienceQuestion -> generateScienceQuestion()
            is ChallengeType.Mindfulness -> generateMindfulness()
        }
    }

    private fun generateAddition(): Challenge.MathChallenge {
        val num1 = Random.nextInt(100, 1000)
        val num2 = Random.nextInt(100, 1000)
        return Challenge.MathChallenge(
            num1 = num1,
            num2 = num2,
            operation = "+",
            correctAnswer = num1 + num2
        )
    }

    private fun generateMultiplication(): Challenge.MathChallenge {
        val num1 = Random.nextInt(100, 1000)
        val num2 = Random.nextInt(100, 1000)
        return Challenge.MathChallenge(
            num1 = num1,
            num2 = num2,
            operation = "×",
            correctAnswer = num1 * num2
        )
    }

    private fun generatePresetMessage(userMessage: String?): Challenge.MessageChallenge {
        val message = userMessage ?: "Stay focused and keep going!"
        return Challenge.MessageChallenge(message = message)
    }

    private fun generateRandomMessage(): Challenge.MessageChallenge {
        val quote = QuoteBank.motivationalQuotes.random()
        return Challenge.MessageChallenge(message = quote)
    }

    private fun generateScienceQuestion(): Challenge.QuizChallenge {
        val question = ScienceBank.questions.random()
        return Challenge.QuizChallenge(
            question = question.question,
            options = question.options,
            correctIndex = question.correctIndex
        )
    }

    private fun generateMindfulness(): Challenge.MessageChallenge {
        val prompt = QuoteBank.mindfulnessPrompts.random()
        return Challenge.MessageChallenge(
            message = prompt,
            requiresHold = true,
            holdDurationMs = 5000
        )
    }
}
