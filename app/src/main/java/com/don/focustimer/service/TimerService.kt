package com.don.focustimer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.don.focustimer.R

class TimerService : Service() {

    companion object {
        const val CHANNEL_ID = "focus_timer_channel"
        const val ALARM_CHANNEL_ID = "focus_timer_alarm_channel"
        const val NOTIFICATION_ID = 1
        const val ALARM_NOTIFICATION_ID = 2
        const val ACTION_START = "com.don.focustimer.START"
        const val ACTION_STOP = "com.don.focustimer.STOP"
        const val ACTION_TIMER_FINISHED = "com.don.focustimer.TIMER_FINISHED"
        const val EXTRA_DURATION_MS = "duration_ms"

        var onTick: ((Long) -> Unit)? = null
        var onFinish: (() -> Unit)? = null
        var isRunning: Boolean = false
            private set
        var remainingTimeMs: Long = 0L
            private set
    }

    private var countDownTimer: CountDownTimer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMs = intent.getLongExtra(EXTRA_DURATION_MS, 0L)
                if (durationMs > 0) {
                    startForegroundTimer(durationMs)
                }
            }
            ACTION_STOP -> {
                stopTimer()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundTimer(durationMs: Long) {
        val notification = buildTimerNotification(durationMs)
        startForeground(NOTIFICATION_ID, notification)
        isRunning = true
        remainingTimeMs = durationMs

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationMs, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMs = millisUntilFinished
                onTick?.invoke(millisUntilFinished)

                // Update notification every second
                if (millisUntilFinished % 1000 < 150) {
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.notify(NOTIFICATION_ID, buildTimerNotification(millisUntilFinished))
                }
            }

            override fun onFinish() {
                remainingTimeMs = 0
                isRunning = false
                startAlarm()
                onFinish?.invoke()
            }
        }.start()
    }

    private fun startAlarm() {
        // Vibrate pattern
        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }

        // Show alarm notification
        val alarmNotification = buildAlarmNotification()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(ALARM_NOTIFICATION_ID, alarmNotification)
    }

    fun stopAlarm() {
        vibrator?.cancel()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(ALARM_NOTIFICATION_ID)
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false
        remainingTimeMs = 0
        vibrator?.cancel()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(ALARM_NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannels() {
        val timerChannel = NotificationChannel(
            CHANNEL_ID,
            "Timer Countdown",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows the active timer countdown"
        }

        val alarmChannel = NotificationChannel(
            ALARM_CHANNEL_ID,
            "Timer Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts when the timer finishes"
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            setSound(alarmSound, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
            enableVibration(true)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(timerChannel)
        manager.createNotificationChannel(alarmChannel)
    }

    private fun buildTimerNotification(remainingMs: Long): Notification {
        val minutes = (remainingMs / 1000) / 60
        val seconds = (remainingMs / 1000) % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)

        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus Timer")
            .setContentText("Time remaining: $timeText")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun buildAlarmNotification(): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setContentTitle("Timer Complete!")
            .setContentText("Complete your challenge to dismiss")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }
}
