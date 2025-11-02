# Slope OS

A custom Android launcher with a premium liquid‑glass aesthetic, built with Kotlin and Jetpack Compose.

## Features
- Elegant splash screen with frosted‑glass "Open Launcher" button and haptics
- Home screen: 5×7 grid, rounded frosted icons, minimal typography (names hidden by default)
- Dock: 2 left icons, center circular drawer, 2 right icons
- App drawer: frosted bubble with smooth elastic animation; 5×7 grid; apps sorted alphabetically
- Long‑press: popup with "Remove from Home" / "App Info"; optional drag & drop rearrangement scaffolding
- Top bar: date (left), glass buttons (center) for Notifications and Home Center, status (right)
- Dynamic effects: wallpaper blur sync (approx.), subtle glow, haptic feedback
- Slope Settings Panel stub: adjust icon size, blur/opacity, vibration, reset layout (persisted via DataStore)

## Status
The project compiles with latest Android Gradle Plugin and Compose. Some system‑level toggles (Wi‑Fi, airplane mode) are linked to system panels due to platform restrictions.

## Build
1. Open in Android Studio (Giraffe+ recommended)
2. Let it install the Android Gradle Plugin and SDKs
3. Select "app" run configuration and build/run on a device

To build an APK from CLI (with Gradle wrapper installed by Studio):

```bash
./gradlew assembleDebug
```

## Launcher setup
- Install the APK on your device
- Press Home and choose "Slope OS" as the default launcher

## Notification drawer
Grant Notification Access: Settings → Notifications → Notification access → enable "Slope OS".

## License
MIT
