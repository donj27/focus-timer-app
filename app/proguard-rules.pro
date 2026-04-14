# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep challenge types for reflection
-keep class com.don.focustimer.challenge.** { *; }

# Keep billing classes
-keep class com.android.vending.billing.** { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }
