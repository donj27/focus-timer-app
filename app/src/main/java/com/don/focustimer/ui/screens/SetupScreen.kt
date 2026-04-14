package com.don.focustimer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.don.focustimer.ads.AdManager
import com.don.focustimer.challenge.ChallengeType
import com.don.focustimer.ui.TimerUiState
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun SetupScreen(
    uiState: TimerUiState,
    isPremium: Boolean,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit,
    onChallengeTypeSelected: (ChallengeType) -> Unit,
    onPresetMessageChange: (String) -> Unit,
    onStartTimer: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val totalDuration = uiState.durationMinutes * 60 + uiState.durationSeconds

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Focus Timer",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        )

        // Duration picker section
        Text(
            text = "Set Duration",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.durationMinutes.toString(),
                onValueChange = { value ->
                    val parsed = value.filter { it.isDigit() }.take(2).toIntOrNull() ?: 0
                    onMinutesChange(parsed.coerceIn(0, 99))
                },
                label = { Text("Minutes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = uiState.durationSeconds.toString(),
                onValueChange = { value ->
                    val parsed = value.filter { it.isDigit() }.take(2).toIntOrNull() ?: 0
                    onSecondsChange(parsed.coerceIn(0, 59))
                },
                label = { Text("Seconds") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Challenge type selector
        Text(
            text = "End-of-Timer Challenge",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChallengeType.all().forEach { challengeType ->
                val isSelected = uiState.selectedChallengeType == challengeType
                val isLocked = challengeType.isPremium && !isPremium

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isLocked) {
                                onUpgradeClick()
                            } else {
                                onChallengeTypeSelected(challengeType)
                            }
                        },
                    border = if (isSelected) BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    ) else null,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                if (isLocked) {
                                    onUpgradeClick()
                                } else {
                                    onChallengeTypeSelected(challengeType)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = challengeType.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = challengeType.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Premium",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Preset message text field
        if (uiState.selectedChallengeType is ChallengeType.PresetMessage) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.presetMessage,
                onValueChange = onPresetMessageChange,
                label = { Text("Your Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start button
        Button(
            onClick = onStartTimer,
            enabled = totalDuration > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Start Timer",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Banner ad (free users only)
        if (!isPremium) {
            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = AdManager.BANNER_AD_UNIT_ID
                        loadAd(AdManager.createAdRequest())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
