package com.don.focustimer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsageEntity(
    @PrimaryKey val packageName: String,
    val usedMillis: Long,        // accumulated usage in current period
    val periodStart: Long,       // epoch ms of period start (day or week)
    val isBlocked: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
