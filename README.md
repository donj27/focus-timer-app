# App Guardian — Addiction Blocker

App Guardian helps you break addictive app habits by enforcing time limits with puzzle challenges. When your daily or weekly budget for an app runs out, App Guardian locks it behind a puzzle — making it genuinely hard to relapse.

## How It Works

1. **Add an app** — Pick any installed app you want to limit
2. **Set a budget** — Choose daily or weekly time allowance (e.g., 30 min/day)
3. **Choose a puzzle** — Select the challenge type that will gate access when time is up
4. **Stay focused** — When the limit is hit, a puzzle overlay blocks the app until solved

## Required Permissions

App Guardian needs several special permissions that must be granted manually:

### 1. Usage Access (Required)
**Why:** Tracks how long each app is used  
**How to grant:** Settings → Apps → Special app access → Usage access → App Guardian → Enable

### 2. Display over other apps (Required)
**Why:** Shows the puzzle lock screen on top of the blocked app  
**How to grant:** Settings → Apps → Special app access → Display over other apps → App Guardian → Enable

### 3. Notification permission (Required on Android 13+)
**Why:** Shows the persistent monitoring notification  
**How to grant:** Granted during first app launch via system dialog

The app will show warning banners for any missing permissions with direct links to the settings pages.

## Building

### Prerequisites
- JDK 17+
- Android SDK 34
- Android Studio Hedgehog or newer

### Build debug APK
```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Architecture

- **AppMonitorService** — Foreground service that polls `UsageStatsManager` every 30 seconds. Triggers puzzle overlay when a limit is exceeded.
- **PuzzleOverlayActivity** — Full-screen Activity shown when a limit is hit. Back button disabled. Must solve puzzle to dismiss.
- **BootReceiver** — Restarts `AppMonitorService` after device reboot.
- **Room database** — Stores app limits and usage state persistently.

## Puzzle Types

| Type | Difficulty | Premium |
|------|-----------|---------|
| 3-digit Addition | Medium | Free |
| 3-digit Multiplication | Hard | Premium |
| Science Quiz | Hard | Premium |
| Mindfulness Hold | Medium | Premium |
| Preset Message | Easy | Free |
| Random Quote | Easy | Free |

## Bypass Resistance

- Back button is disabled during puzzle
- Activity excluded from recent apps
- Screen turns on when limit is hit
- Monitor service re-triggers puzzle if blocked app is opened again
- `START_STICKY` service restarts if killed by OS
