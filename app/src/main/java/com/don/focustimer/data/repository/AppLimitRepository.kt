package com.don.focustimer.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.don.focustimer.data.db.AppDatabase
import com.don.focustimer.data.db.AppLimitEntity
import com.don.focustimer.data.db.AppUsageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class AppLimitRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val limitDao = db.appLimitDao()
    private val usageDao = db.appUsageDao()

    data class AppLimitWithUsage(
        val limit: AppLimitEntity,
        val usedMillis: Long,
        val isBlocked: Boolean,
        val periodStart: Long
    )

    fun getLimitsWithUsage(): Flow<List<AppLimitWithUsage>> {
        return combine(limitDao.getAllLimits(), usageDao.getAllUsage()) { limits, usages ->
            val usageMap = usages.associateBy { it.packageName }
            limits.map { limit ->
                val usage = usageMap[limit.packageName]
                val periodStart = getPeriodStart(limit.periodType)
                val usedMillis = if (usage != null && usage.periodStart >= periodStart) {
                    usage.usedMillis
                } else 0L
                AppLimitWithUsage(
                    limit = limit,
                    usedMillis = usedMillis,
                    isBlocked = usage?.isBlocked ?: false,
                    periodStart = periodStart
                )
            }
        }
    }

    suspend fun addAppLimit(
        packageName: String,
        limitMinutes: Int,
        periodType: String,
        challengeType: String
    ) {
        val appName = getAppName(packageName)
        limitDao.upsert(
            AppLimitEntity(
                packageName = packageName,
                appName = appName,
                limitMinutes = limitMinutes,
                periodType = periodType,
                challengeType = challengeType
            )
        )
        // Initialize usage record if not exists
        if (usageDao.getUsageByPackage(packageName) == null) {
            usageDao.upsert(
                AppUsageEntity(
                    packageName = packageName,
                    usedMillis = 0L,
                    periodStart = getPeriodStart(periodType)
                )
            )
        }
    }

    suspend fun removeAppLimit(packageName: String) {
        limitDao.deleteByPackage(packageName)
    }

    suspend fun setBlocked(packageName: String, blocked: Boolean) {
        usageDao.setBlocked(packageName, blocked)
    }

    suspend fun refreshUsageFromSystem(packageName: String, periodType: String) {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val periodStart = getPeriodStart(periodType)
        val now = System.currentTimeMillis()
        val stats = usm.queryAndAggregateUsageStats(periodStart, now)
        val usedMillis = stats[packageName]?.totalTimeInForeground ?: 0L
        val existing = usageDao.getUsageByPackage(packageName)
        usageDao.upsert(
            AppUsageEntity(
                packageName = packageName,
                usedMillis = usedMillis,
                periodStart = periodStart,
                isBlocked = existing?.isBlocked ?: false
            )
        )
    }

    fun getInstalledApps(): List<Pair<String, String>> {
        val pm = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        return pm.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName to getAppName(it.activityInfo.packageName) }
            .distinctBy { it.first }
            .sortedBy { it.second }
    }

    private fun getAppName(packageName: String): String {
        return try {
            val pm = context.packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    private fun getPeriodStart(periodType: String): Long {
        val cal = Calendar.getInstance()
        return if (periodType == "weekly") {
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
    }
}
