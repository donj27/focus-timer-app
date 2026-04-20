package com.don.focustimer

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.don.focustimer.ads.AdManager
import com.don.focustimer.service.AppMonitorService
import com.don.focustimer.ui.InstalledApp
import com.don.focustimer.ui.MainViewModel
import com.don.focustimer.ui.screens.AppPickerScreen
import com.don.focustimer.ui.screens.HomeScreen
import com.don.focustimer.ui.screens.LimitConfigScreen
import com.don.focustimer.ui.theme.FocusTimerTheme

sealed class Screen {
    object Home : Screen()
    object AppPicker : Screen()
    data class LimitConfig(val app: InstalledApp) : Screen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdManager.initialize(this)
        AppMonitorService.start(this)

        setContent {
            FocusTimerTheme {
                AppGuardianApp(
                    viewModel = viewModel,
                    hasUsagePermission = hasUsageStatsPermission(),
                    hasOverlayPermission = Settings.canDrawOverlays(this),
                    onRequestUsagePermission = {
                        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    },
                    onRequestOverlayPermission = {
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                            )
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Restart service if killed
        AppMonitorService.start(this)
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

@Composable
fun AppGuardianApp(
    viewModel: MainViewModel,
    hasUsagePermission: Boolean,
    hasOverlayPermission: Boolean,
    onRequestUsagePermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val limitsWithUsage by viewModel.limitsWithUsage.collectAsStateWithLifecycle(emptyList())
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val isPremium by viewModel.billingManager.isPremium.collectAsStateWithLifecycle()

    when (val screen = currentScreen) {
        is Screen.Home -> HomeScreen(
            limitsWithUsage = limitsWithUsage,
            hasUsagePermission = hasUsagePermission,
            hasOverlayPermission = hasOverlayPermission,
            onAddAppClick = {
                viewModel.loadInstalledApps()
                currentScreen = Screen.AppPicker
            },
            onRemoveApp = { packageName -> viewModel.removeAppLimit(packageName) },
            onRequestUsagePermission = onRequestUsagePermission,
            onRequestOverlayPermission = onRequestOverlayPermission
        )

        is Screen.AppPicker -> {
            val alreadyLimited = limitsWithUsage.map { it.limit.packageName }.toSet()
            AppPickerScreen(
                apps = installedApps,
                alreadyLimited = alreadyLimited,
                onAppSelected = { app -> currentScreen = Screen.LimitConfig(app) },
                onBack = { currentScreen = Screen.Home }
            )
        }

        is Screen.LimitConfig -> LimitConfigScreen(
            appName = screen.app.appName,
            packageName = screen.app.packageName,
            isPremium = isPremium,
            onSave = { limitMinutes, periodType, challengeType ->
                viewModel.addAppLimit(
                    packageName = screen.app.packageName,
                    limitMinutes = limitMinutes,
                    periodType = periodType,
                    challengeType = challengeType
                )
                currentScreen = Screen.Home
            },
            onBack = { currentScreen = Screen.AppPicker }
        )
    }
}
