package com.don.focustimer.ui.puzzle

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.don.focustimer.challenge.Challenge
import kotlinx.coroutines.delay

@Composable
fun PuzzleScreen(
    appName: String,
    challenge: Challenge,
    onSolved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Error auto-clear
    LaunchedEffect(showError) {
        if (showError) {
            delay(2000)
            showError = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Lock icon header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "TIME LIMIT REACHED",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                appName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "is locked for today",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(32.dp))

            // Puzzle content
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        "SOLVE TO UNLOCK",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    when (challenge) {
                        is Challenge.MathChallenge -> MathChallengeContent(
                            challenge = challenge,
                            onSolved = onSolved,
                            onError = { msg ->
                                errorMessage = msg
                                showError = true
                            }
                        )
                        is Challenge.QuizChallenge -> QuizChallengeContent(
                            challenge = challenge,
                            onSolved = onSolved,
                            onError = { msg ->
                                errorMessage = msg
                                showError = true
                            }
                        )
                        is Challenge.MessageChallenge -> MessageChallengeContent(
                            challenge = challenge,
                            onSolved = onSolved
                        )
                    }
                }
            }

            // Error message
            AnimatedVisibility(
                visible = showError,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "Solve the puzzle above to regain access.\nNo shortcuts — commit to the limit.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MathChallengeContent(
    challenge: Challenge.MathChallenge,
    onSolved: () -> Unit,
    onError: (String) -> Unit
) {
    var answer by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun submit() {
        val userAnswer = answer.trim().toIntOrNull()
        if (userAnswer == null) {
            onError("Please enter a number")
            return
        }
        if (userAnswer == challenge.correctAnswer) {
            keyboardController?.hide()
            onSolved()
        } else {
            answer = ""
            onError("Wrong answer — try again!")
        }
    }

    Text(
        "${challenge.num1} ${challenge.operation} ${challenge.num2} = ?",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(20.dp))

    OutlinedTextField(
        value = answer,
        onValueChange = { answer = it.filter { c -> c.isDigit() } },
        label = { Text("Your answer") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    )

    Spacer(Modifier.height(16.dp))

    Button(
        onClick = { submit() },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text("SUBMIT", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun QuizChallengeContent(
    challenge: Challenge.QuizChallenge,
    onSolved: () -> Unit,
    onError: (String) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(-1) }

    Text(
        challenge.question,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(Modifier.height(16.dp))

    challenge.options.forEachIndexed { index, option ->
        val isSelected = selectedIndex == index
        OutlinedButton(
            onClick = {
                selectedIndex = index
                if (index == challenge.correctIndex) {
                    onSolved()
                } else {
                    onError("Not quite — try another answer")
                    selectedIndex = -1
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    Color.Transparent
            ),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline
            )
        ) {
            Text(
                option,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun MessageChallengeContent(
    challenge: Challenge.MessageChallenge,
    onSolved: () -> Unit
) {
    var holdProgress by remember { mutableStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }

    LaunchedEffect(isHolding) {
        if (challenge.requiresHold && isHolding) {
            val steps = 50
            val stepDelay = challenge.holdDurationMs / steps
            for (i in 1..steps) {
                delay(stepDelay)
                holdProgress = i.toFloat() / steps
                if (holdProgress >= 1f) {
                    onSolved()
                    break
                }
            }
            if (!isHolding) holdProgress = 0f
        }
    }

    Text(
        challenge.message,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(Modifier.height(20.dp))

    if (challenge.requiresHold) {
        LinearProgressIndicator(
            progress = { holdProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            isHolding = event.changes.any { it.pressed }
                        }
                    }
                },
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("HOLD TO ACKNOWLEDGE", style = MaterialTheme.typography.labelLarge)
        }
    } else {
        Button(
            onClick = onSolved,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("I ACKNOWLEDGE THIS", style = MaterialTheme.typography.labelLarge)
        }
    }
}
