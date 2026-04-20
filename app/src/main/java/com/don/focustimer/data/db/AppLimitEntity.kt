package com.don.focustimer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_limits")
data class AppLimitEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val limitMinutes: Int,       // daily/weekly limit in minutes
    val periodType: String,      // "daily" or "weekly"
    val challengeType: String,   // matches ChallengeType class name
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
