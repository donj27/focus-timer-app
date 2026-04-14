# Focus Timer

An Android timer app that forces intentional engagement when the timer fires. Instead of a simple tap-to-dismiss alarm, the user must complete a challenge — making each timer's end a micro-ritual that requires brain activation.

## Setup

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17+
- Android SDK 34

### Build & Install
```bash
./gradlew installDebug
```

### AdMob Setup
The app ships with Google's **test AdMob app ID** (`ca-app-pub-3940256099942544~3347511713`) and test banner ad unit ID so it runs without crashing during development. Before publishing to the Play Store:
1. Replace the AdMob app ID in `AndroidManifest.xml` with your production app ID:
   ```xml
   <meta-data
       android:name="com.google.android.gms.ads.APPLICATION_ID"
       android:value="YOUR_REAL_ADMOB_APP_ID" />
   ```
2. Replace the test banner ad unit ID in `AdManager.kt` with your production ad unit ID.

### In-App Purchase Setup
- Product ID: `focus_timer_premium`
- Type: One-time (non-consumable) in-app purchase
- Price: $1.99–$2.99 (configure in Google Play Console)

## Challenge Types

### Free Tier
| Challenge | Description |
|-----------|-------------|
| **3-Digit Addition** | Solve a random addition problem with two 3-digit numbers (100–999). Fresh problem every time. |
| **Preset Message** | Display your custom message. Tap "I Acknowledge" to dismiss. |
| **Random Message** | Display a random motivational quote from a bundled bank. Tap to acknowledge. |

### Premium Tier (unlocked via one-time purchase)
| Challenge | Description |
|-----------|-------------|
| **3-Digit Multiplication** | Solve a random multiplication problem with two 3-digit numbers. |
| **Science Question** | Answer a multiple-choice science trivia question (physics, chemistry, biology, astronomy). |
| **Mindfulness** | Read a calming prompt and hold a button for 5 seconds to dismiss. |

## Monetization

- **Free tier**: 3 challenge types + banner ad on setup screen
- **Premium tier**: One-time in-app purchase (~$1.99–$2.99) unlocks all 6 challenge types and removes ads
- Ads only appear on the setup screen — never during an active countdown or challenge
- Banner ads via **Google AdMob**
- In-app purchases via **Google Play Billing Library**

## Features
- Foreground service keeps timer alive when navigating away
- Screen rotation handled without losing timer state
- All challenges generated locally — no network required
- Offline-safe: timer and challenges work fully offline; ads simply don't show
- No bypass — the only way to dismiss the alarm is completing the challenge

## Architecture
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material3
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34
- **Timer**: Android `CountDownTimer` in a foreground service
- **State**: ViewModel + StateFlow
