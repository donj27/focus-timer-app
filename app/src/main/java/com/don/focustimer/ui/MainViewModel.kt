package com.don.focustimer.ui

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.don.focustimer.billing.BillingManager
import com.don.focustimer.challenge.ChallengeType
import com.don.focustimer.data.repository.AppLimitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InstalledApp(
    val packageName: String,
    val appName: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppLimitRepository(application)
    val billingManager = BillingManager(application)

    val limitsWithUsage = repository.getLimitsWithUsage()

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    init {
        billingManager.initialize()
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                repository.getInstalledApps()
                    .map { (pkg, name) -> InstalledApp(pkg, name) }
            }
            _installedApps.value = apps
        }
    }

    fun addAppLimit(
        packageName: String,
        limitMinutes: Int,
        periodType: String,
        challengeType: String
    ) {
        viewModelScope.launch {
            repository.addAppLimit(packageName, limitMinutes, periodType, challengeType)
        }
    }

    fun removeAppLimit(packageName: String) {
        viewModelScope.launch {
            repository.removeAppLimit(packageName)
        }
    }

    fun getChallengeTypes(isPremium: Boolean): List<ChallengeType> {
        return if (isPremium) ChallengeType.all() else ChallengeType.free()
    }

    fun getAppIcon(packageName: String) = try {
        getApplication<Application>().packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}
