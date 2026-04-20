package com.don.focustimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.don.focustimer.MainActivity
import com.don.focustimer.data.db.AppDatabase
import com.don.focustimer.data.db.AppLimitEntity
import com.don.focustimer.data.db.AppUsageEntity
import com.don.focustimer.ui.puzzle.PuzzleOverlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AppMonitorService : LifecycleService() {

    companion object {
        const val CHANNEL_ID = "app_guardian_monitor"
        const val NOTIFICATION_ID = 1001
        const val POLL_INTERVAL_MS = 30_000L  // 30 seconds
        const val ACTION_STOP = "com.don.focustimer.STOP_MONITOR"

        fun start(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private var monitorJob: Job? = null
    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getInstance(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        startMonitoring()
        return START_STICKY
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        super.onDestroy()
    }

    private fun startMonitoring() {
        monitorJob?.cancel()
        monitorJob = lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    checkAndEnforceLimits()
                } catch (e: Exception) {
                    Log.e("AppMonitor", "Error checking limits", e)
                }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private suspend fun checkAndEnforceLimits() {
        val limits = db.appLimitDao().getActiveLimits().first()
        if (limits.isEmpty()) return

        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()

        for (limit in limits) {
            val periodStart = getPeriodStart(limit.periodType)
            val stats = usm.queryAndAggregateUsageStats(periodStart, now)
            val usedMillis = stats[limit.packageName]?.totalTimeInForeground ?: 0L
            val limitMillis = limit.limitMinutes * 60_000L

            // Update usage in DB
            val existingUsage = db.appUsageDao().getUsageByPackage(limit.packageName)
            val wasBlocked = existingUsage?.isBlocked ?: false

            val newUsage = AppUsageEntity(
                packageName = limit.packageName,
                usedMillis = usedMillis,
                periodStart = periodStart,
                isBlocked = usedMillis >= limitMillis
            )
            db.appUsageDao().upsert(newUsage)

            // Trigger overlay if limit just exceeded and app is in foreground
            if (usedMillis >= limitMillis && !wasBlocked) {
                val foregroundApp = getForegroundApp(usm, now)
                if (foregroundApp == limit.packageName) {
                    triggerPuzzleOverlay(limit)
                }
            } else if (usedMillis >= limitMillis && wasBlocked) {
                // Still blocked — check if user is trying to use the app
                val foregroundApp = getForegroundApp(usm, now)
                if (foregroundApp == limit.packageName) {
                    triggerPuzzleOverlay(limit)
                }
            }
        }
    }

    private fun getForegroundApp(usm: UsageStatsManager, now: Long): String? {
        val recentEvents = usm.queryEvents(now - 5000, now)
        val event = android.app.usage.UsageEvents.Event()
        var foreground: String? = null
        while (recentEvents.hasNextEvent()) {
            recentEvents.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                foreground = event.packageName
            }
        }
        return foreground
    }

    private fun triggerPuzzleOverlay(limit: AppLimitEntity) {
        Log.d("AppMonitor", "Triggering puzzle for ${limit.packageName}")
        val intent = Intent(this, PuzzleOverlayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(PuzzleOverlayActivity.EXTRA_PACKAGE_NAME, limit.packageName)
            putExtra(PuzzleOverlayActivity.EXTRA_APP_NAME, limit.appName)
            putExtra(PuzzleOverlayActivity.EXTRA_CHALLENGE_TYPE, limit.challengeType)
        }
        startActivity(intent)
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(com.don.focustimer.R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(com.don.focustimer.R.string.channel_description)
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val mainIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(com.don.focustimer.R.string.notification_title))
            .setContentText(getString(com.don.focustimer.R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
