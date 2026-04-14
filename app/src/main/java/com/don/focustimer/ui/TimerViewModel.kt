package com.don.focustimer.ui

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.don.focustimer.billing.BillingManager
import com.don.focustimer.challenge.*
import com.don.focustimer.service.TimerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TimerState {
    SETUP,
    RUNNING,
    CHALLENGE
}

data class TimerUiState(
    val timerState: TimerState = TimerState.SETUP,
    val durationMinutes: Int = 5,
    val durationSeconds: Int = 0,
    val remainingTimeMs: Long = 0L,
    val selectedChallengeType: ChallengeType = ChallengeType.Addition,
    val currentChallenge: Challenge? = null,
    val userAnswer: String = "",
    val answerError: Boolean = false,
    val presetMessage: String = "Time's up! Get back to work!",
    val holdProgress: Float = 0f,
    val selectedQuizAnswer: Int = -1
)

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    val billingManager = BillingManager(application)

    init {
        billingManager.initialize()

        TimerService.onTick = { remainingMs ->
            _uiState.value = _uiState.value.copy(remainingTimeMs = remainingMs)
        }

        TimerService.onFinish = {
            val challenge = ChallengeGenerator.generate(
                _uiState.value.selectedChallengeType,
                _uiState.value.presetMessage
            )
            _uiState.value = _uiState.value.copy(
                timerState = TimerState.CHALLENGE,
                currentChallenge = challenge,
                remainingTimeMs = 0L,
                userAnswer = "",
                answerError = false,
                holdProgress = 0f,
                selectedQuizAnswer = -1
            )
        }

        // Restore running timer state if service is active
        if (TimerService.isRunning) {
            _uiState.value = _uiState.value.copy(
                timerState = TimerState.RUNNING,
                remainingTimeMs = TimerService.remainingTimeMs
            )
        }
    }

    fun setDurationMinutes(minutes: Int) {
        _uiState.value = _uiState.value.copy(durationMinutes = minutes.coerceIn(0, 99))
    }

    fun setDurationSeconds(seconds: Int) {
        _uiState.value = _uiState.value.copy(durationSeconds = seconds.coerceIn(0, 59))
    }

    fun selectChallengeType(type: ChallengeType) {
        _uiState.value = _uiState.value.copy(selectedChallengeType = type)
    }

    fun setPresetMessage(message: String) {
        _uiState.value = _uiState.value.copy(presetMessage = message)
    }

    fun startTimer() {
        val state = _uiState.value
        val totalMs = (state.durationMinutes * 60L + state.durationSeconds) * 1000L
        if (totalMs <= 0) return

        _uiState.value = state.copy(
            timerState = TimerState.RUNNING,
            remainingTimeMs = totalMs
        )

        val context = getApplication<Application>()
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_DURATION_MS, totalMs)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun cancelTimer() {
        val context = getApplication<Application>()
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)

        _uiState.value = _uiState.value.copy(
            timerState = TimerState.SETUP,
            remainingTimeMs = 0L
        )
    }

    fun updateUserAnswer(answer: String) {
        _uiState.value = _uiState.value.copy(userAnswer = answer, answerError = false)
    }

    fun selectQuizAnswer(index: Int) {
        _uiState.value = _uiState.value.copy(selectedQuizAnswer = index)
    }

    fun submitAnswer(): Boolean {
        val state = _uiState.value
        val challenge = state.currentChallenge ?: return false

        return when (challenge) {
            is Challenge.MathChallenge -> {
                val userNum = state.userAnswer.trim().toIntOrNull()
                if (userNum == challenge.correctAnswer) {
                    dismissChallenge()
                    true
                } else {
                    _uiState.value = state.copy(answerError = true)
                    false
                }
            }
            is Challenge.MessageChallenge -> {
                if (!challenge.requiresHold) {
                    dismissChallenge()
                    true
                } else {
                    false
                }
            }
            is Challenge.QuizChallenge -> {
                if (state.selectedQuizAnswer == challenge.correctIndex) {
                    dismissChallenge()
                    true
                } else {
                    _uiState.value = state.copy(answerError = true, selectedQuizAnswer = -1)
                    false
                }
            }
        }
    }

    fun updateHoldProgress(progress: Float) {
        _uiState.value = _uiState.value.copy(holdProgress = progress)
        if (progress >= 1f) {
            dismissChallenge()
        }
    }

    private fun dismissChallenge() {
        // Stop alarm
        val context = getApplication<Application>()
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)

        _uiState.value = TimerUiState(
            presetMessage = _uiState.value.presetMessage,
            selectedChallengeType = _uiState.value.selectedChallengeType,
            durationMinutes = _uiState.value.durationMinutes,
            durationSeconds = _uiState.value.durationSeconds
        )
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.destroy()
        TimerService.onTick = null
        TimerService.onFinish = null
    }
}
