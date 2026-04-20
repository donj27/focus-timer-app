package com.don.focustimer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.don.focustimer.challenge.ChallengeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitConfigScreen(
    appName: String,
    packageName: String,
    isPremium: Boolean,
    onSave: (limitMinutes: Int, periodType: String, challengeType: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var limitHours by remember { mutableStateOf(0) }
    var limitMinutes by remember { mutableStateOf(30) }
    var periodType by remember { mutableStateOf("daily") }
    var selectedChallengeType by remember { mutableStateOf<ChallengeType>(ChallengeType.Addition) }

    val totalMinutes = limitHours * 60 + limitMinutes
    val isValid = totalMinutes > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Set Limit", style = MaterialTheme.typography.titleMedium)
                        Text(appName, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Time limit section
            SectionCard(title = "TIME LIMIT") {
                Text(
                    "How much time is allowed per period?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours spinner
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("HOURS", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        NumberPicker(
                            value = limitHours,
                            range = 0..23,
                            onValueChange = { limitHours = it }
                        )
                    }
                    Text(":", style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MINUTES", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        NumberPicker(
                            value = limitMinutes,
                            range = 0..59,
                            onValueChange = { limitMinutes = it }
                        )
                    }
                }
            }

            // Period section
            SectionCard(title = "RESETS") {
                Column(Modifier.selectableGroup()) {
                    listOf("daily" to "Every day at midnight", "weekly" to "Every week on Monday").forEach { (type, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = periodType == type,
                                    onClick = { periodType = type },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = periodType == type, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(type.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleSmall)
                                Text(desc, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Challenge type section
            SectionCard(title = "UNLOCK PUZZLE") {
                Text(
                    "Solve this puzzle to unlock the app when time is up.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                val challengeTypes = ChallengeType.all()
                challengeTypes.forEach { ct ->
                    val enabled = isPremium || !ct.isPremium
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedChallengeType == ct,
                                onClick = { if (enabled) selectedChallengeType = ct },
                                role = Role.RadioButton,
                                enabled = enabled
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedChallengeType == ct,
                            onClick = null,
                            enabled = enabled
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    ct.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (enabled)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                                if (ct.isPremium) {
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = "Premium",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                ct.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (enabled)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            }

            // Save button
            Button(
                onClick = {
                    onSave(totalMinutes, periodType, selectedChallengeType::class.simpleName ?: "Addition")
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    "PROTECT THIS APP",
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextButton(
                onClick = { if (value < range.last) onValueChange(value + 1) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) { Text("▲", style = MaterialTheme.typography.bodySmall) }

            Text(
                value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            TextButton(
                onClick = { if (value > range.first) onValueChange(value - 1) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) { Text("▼", style = MaterialTheme.typography.bodySmall) }
        }
    }
}
