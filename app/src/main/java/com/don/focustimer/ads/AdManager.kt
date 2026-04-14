package com.don.focustimer.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object AdManager {
    // Test ad unit ID - replace with real one for production
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    fun initialize(context: Context) {
        MobileAds.initialize(context) { }
    }

    fun createAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }
}
