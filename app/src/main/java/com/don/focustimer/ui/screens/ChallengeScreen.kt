package com.don.focustimer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.don.focustimer.challenge.Challenge
import com.don.focustimer.ui.TimerUiState
import kotlinx.coroutines.delay

@Composable
fun ChallengeScreen(
    uiState: TimerUiState,
    onAnswerChange: (String) -> Unit,
    onSubmitAnswer: () -> Boolean,
    onQuizAnswerSelected: (Int) -> Unit,
    onHoldProgressUpdate: (Float) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Time's Up!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            when (val challenge = uiState.currentChallenge) {
                is Challenge.MathChallenge -> MathChallengeContent(
                    challenge = challenge,
                    userAnswer = uiState.userAnswer,
                    answerError = uiState.answerError,
                    onAnswerChange = onAnswerChange,
                    onSubmitAnswer = onSubmitAnswer
                )
                is Challenge.MessageChallenge -> {
                    if (challenge.requiresHold) {
                        HoldChallengeContent(
                            challenge = challenge,
                            holdProgress = uiState.holdProgress,
                            onHoldProgressUpdate = onHoldProgressUpdate
                        )
                    } else {
                        MessageChallengeContent(
                            challenge = challenge,
                            onSubmitAnswer = onSubmitAnswer
                        )
                    }
                }
                is Challenge.QuizChallenge -> QuizChallengeContent(
                    challenge = challenge,
                    selectedQuizAnswer = uiState.selectedQuizAnswer,
                    answerError = uiState.answerError,
                    onQuizAnswerSelected = onQuizAnswerSelected,
                    onSubmitAnswer = onSubmitAnswer
                )
                null -> {}
            }
        }
    }
}

@Composable
private fun MathChallengeContent(
    challenge: Challenge.MathChallenge,
    userAnswer: String,
    answerError: Boolean,
    onAnswerChange: (String) -> Unit,
    onSubmitAnswer: () -> Boolean
) {
    Text(
        text = "${challenge.num1} ${challenge.operation} ${challenge.num2} = ?",
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onErrorContainer,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = userAnswer,
        onValueChange = onAnswerChange,
        label = { Text("Your Answer") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = answerError,
        modifier = Modifier.width(200.dp)
    )

    if (answerError) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Wrong answer, try again!",
            color = Color.Red,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onSubmitAnswer() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Submit", fontSize = 18.sp)
    }
}

@Composable
private fun MessageChallengeContent(
    challenge: Challenge.MessageChallenge,
    onSubmitAnswer: () -> Boolean
) {
    Text(
        text = challenge.message,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onErrorContainer,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = { onSubmitAnswer() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("I Acknowledge", fontSize = 18.sp)
    }
}

@Composable
private fun HoldChallengeContent(
    challenge: Challenge.MessageChallenge,
    holdProgress: Float,
    onHoldProgressUpdate: (Float) -> Unit
) {
    var isHolding by remember { mutableStateOf(false) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            val holdDuration = challenge.holdDurationMs.takeIf { it > 0 } ?: 5000L
            val steps = 50
            val stepDuration = holdDuration / steps
            for (i in 1..steps) {
                delay(stepDuration)
                if (!isHolding) break
                onHoldProgressUpdate(i.toFloat() / steps)
            }
        } else {
            onHoldProgressUpdate(0f)
        }
    }

    Text(
        text = challenge.message,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onErrorContainer,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(32.dp))

    LinearProgressIndicator(
        progress = { holdProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .padding(horizontal = 32.dp),
        color = MaterialTheme.colorScheme.error,
        trackColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.3f)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = if (isHolding) "Keep holding..." else "Hold to dismiss...",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        tryAwaitRelease()
                        isHolding = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            color = if (isHolding) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.error
            },
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isHolding) "Holding..." else "Press & Hold",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onError,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun QuizChallengeContent(
    challenge: Challenge.QuizChallenge,
    selectedQuizAnswer: Int,
    answerError: Boolean,
    onQuizAnswerSelected: (Int) -> Unit,
    onSubmitAnswer: () -> Boolean
) {
    Text(
        text = challenge.question,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onErrorContainer,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    challenge.options.forEachIndexed { index, option ->
        val isSelected = selectedQuizAnswer == index
        OutlinedButton(
            onClick = { onQuizAnswerSelected(index) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                }
            ),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(
                text = option,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }

    if (answerError) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Wrong answer, try again!",
            color = Color.Red,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onSubmitAnswer() },
        enabled = selectedQuizAnswer >= 0,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Submit", fontSize = 18.sp)
    }
}
