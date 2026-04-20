package com.don.focustimer.ui.puzzle

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.don.focustimer.challenge.Challenge
import com.don.focustimer.challenge.ChallengeGenerator
import com.don.focustimer.challenge.ChallengeType
import com.don.focustimer.data.db.AppDatabase
import com.don.focustimer.ui.theme.FocusTimerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PuzzleOverlayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_CHALLENGE_TYPE = "extra_challenge_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make it really hard to dismiss: show over lock screen, prevent screenshots
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Block back button — user must solve the puzzle
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing — back is disabled during puzzle
            }
        })

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "App"
        val challengeTypeName = intent.getStringExtra(EXTRA_CHALLENGE_TYPE) ?: "Addition"

        val challengeType = resolveChallengeType(challengeTypeName)
        val challenge = ChallengeGenerator.generate(challengeType)

        setContent {
            FocusTimerTheme {
                PuzzleScreen(
                    appName = appName,
                    challenge = challenge,
                    onSolved = { onPuzzleSolved(packageName) }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Already showing — bring to front (called when service tries to show it again)
    }

    private fun onPuzzleSolved(packageName: String) {
        // Unblock the app in DB
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(applicationContext)
            db.appUsageDao().setBlocked(packageName, false)
        }
        finish()
    }

    private fun resolveChallengeType(name: String): ChallengeType {
        return when (name) {
            "Addition" -> ChallengeType.Addition
            "Multiplication" -> ChallengeType.Multiplication
            "ScienceQuestion" -> ChallengeType.ScienceQuestion
            "Mindfulness" -> ChallengeType.Mindfulness
            "RandomMessage" -> ChallengeType.RandomMessage
            "PresetMessage" -> ChallengeType.PresetMessage
            else -> ChallengeType.Addition
        }
    }
}
