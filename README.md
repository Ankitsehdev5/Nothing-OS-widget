# Nothing Music Widget

A minimal Nothing OS-inspired Android home-screen music widget:
- **Only Play/Pause toggle** visibly shown on the right (`#D71921` circular button).
- **Previous / Next track**: swipeable `AdapterViewFlipper` track display plus invisible left/right edge touch zones for reliable previous/next track control across all launchers.
- **Universal media control**: uses `NotificationListenerService` + `MediaSessionManager` to control Spotify, YouTube Music, Apple Music, or any active Android media session.

## Build a Deployable APK

1. Unzip `NothingMusicWidget.zip` and open the folder in **Android Studio** (Hedgehog / Iguana or newer).
2. Let Gradle sync, then select **Build > Build Bundle(s) / APK(s) > Build APK(s)** (or run `gradle assembleDebug` in a terminal with Android SDK 34 installed).
3. Install the generated `app/build/outputs/apk/debug/app-debug.apk` on your phone, open the app once to grant **Notification Access**, and add the **Nothing Music** widget to your home screen.
