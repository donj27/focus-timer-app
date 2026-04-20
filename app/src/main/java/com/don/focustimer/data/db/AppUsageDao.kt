package com.don.focustimer.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Query("SELECT * FROM app_usage")
    fun getAllUsage(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName LIMIT 1")
    suspend fun getUsageByPackage(packageName: String): AppUsageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(usage: AppUsageEntity)

    @Query("UPDATE app_usage SET isBlocked = :blocked WHERE packageName = :packageName")
    suspend fun setBlocked(packageName: String, blocked: Boolean)

    @Query("UPDATE app_usage SET usedMillis = :usedMillis, lastUpdated = :now WHERE packageName = :packageName")
    suspend fun updateUsage(packageName: String, usedMillis: Long, now: Long = System.currentTimeMillis())
}
