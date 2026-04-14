package com.don.focustimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.don.focustimer.ads.AdManager
import com.don.focustimer.ui.TimerState
import com.don.focustimer.ui.TimerViewModel
import com.don.focustimer.ui.screens.ChallengeScreen
import com.don.focustimer.ui.screens.CountdownScreen
import com.don.focustimer.ui.screens.SetupScreen
import com.don.focustimer.ui.theme.FocusTimerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AdMob
        AdManager.initialize(this)

        // Block back press during challenge - the whole point is forced engagement
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val state = viewModel.uiState.value
                when (state.timerState) {
                    TimerState.CHALLENGE -> {
                        // Do nothing - user must complete the challenge
                    }
                    TimerState.RUNNING -> {
                        // Allow canceling timer via back press
                        viewModel.cancelTimer()
                    }
                    TimerState.SETUP -> {
                        // Allow normal back behavior
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })

        setContent {
            FocusTimerTheme {
                val uiState by viewModel.uiState.collectAsState()
                val isPremium by viewModel.billingManager.isPremium.collectAsState()

                when (uiState.timerState) {
                    TimerState.SETUP -> {
                        SetupScreen(
                            uiState = uiState,
                            isPremium = isPremium,
                            onMinutesChange = viewModel::setDurationMinutes,
                            onSecondsChange = viewModel::setDurationSeconds,
                            onChallengeTypeSelected = viewModel::selectChallengeType,
                            onPresetMessageChange = viewModel::setPresetMessage,
                            onStartTimer = viewModel::startTimer,
                            onUpgradeClick = {
                                viewModel.billingManager.launchPurchaseFlow(this)
                            }
                        )
                    }
                    TimerState.RUNNING -> {
                        val totalMs = (uiState.durationMinutes * 60L + uiState.durationSeconds) * 1000L
                        CountdownScreen(
                            uiState = uiState,
                            totalDurationMs = totalMs,
                            onCancel = viewModel::cancelTimer
                        )
                    }
                    TimerState.CHALLENGE -> {
                        ChallengeScreen(
                            uiState = uiState,
                            onAnswerChange = viewModel::updateUserAnswer,
                            onSubmitAnswer = viewModel::submitAnswer,
                            onQuizAnswerSelected = viewModel::selectQuizAnswer,
                            onHoldProgressUpdate = viewModel::updateHoldProgress
                        )
                    }
                }
            }
        }
    }
}
