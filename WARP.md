# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Slope OS is a custom Android launcher built with Kotlin and Jetpack Compose, featuring a premium liquid-glass aesthetic. The launcher provides a minimal, elegant UI with frosted-glass effects, haptic feedback, and dynamic wallpaper blur sync.

## Build and Development Commands

### Building

Build debug APK via Android Studio or Gradle:

```bash
# Open in Android Studio and select "app" run configuration, then build/run
# Note: Gradle wrapper is not committed to this repo - Android Studio will install it
```

After Gradle wrapper is installed by Android Studio:

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install debug build to connected device
```

### Testing

Testing infrastructure is configured but no tests exist yet. Test dependencies include:

- JUnit 4 for unit tests
- Espresso for UI tests
- Compose UI test framework

To run tests (when implemented):

```bash
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests on device/emulator
```

### Cleaning

```bash
./gradlew clean                  # Clean build artifacts
```

## Architecture

### Core Components

**MainActivity** (`MainActivity.kt`)
- Entry point using Jetpack Navigation
- Two routes: `splash` → `home`
- Edge-to-edge window mode enabled

**HomeViewModel** (`viewmodel/HomeViewModel.kt`)
- Central state management for the launcher
- Manages app loading, pinning/unpinning, and launching
- Uses `AppRepository` for app queries and `SettingsDataStore` for persistence

**AppRepository** (`data/AppRepository.kt`)
- Queries Android PackageManager for launchable apps
- Filters out the launcher itself
- Provides app icons via `getAppIcon()`
- Returns sorted `AppEntry` list (alphabetical by label)

**SettingsDataStore** (`data/SettingsDataStore.kt`)
- DataStore-based persistence for:
  - Pinned apps (shown on home screen)
  - Icon size multiplier
  - Blur radius and opacity (for glass effect)
  - Haptics enabled/disabled
- Exposes reactive Flows for all settings

### UI Architecture

**HomeScreen** (`ui/screens/HomeScreen.kt`)
- Main launcher screen with 5×7 grid layout
- Shows pinned apps on home, unpinned apps in drawer
- App drawer is a fullscreen frosted bubble with elastic animation
- Manages panels: Notifications drawer, Quick Settings (Home Center), Slope Settings
- Long-press on icons shows popup menu (Remove from Home / App Info / Pin to Home)
- Long-press on home screen background opens settings panel

**GlassSurface** (`ui/components/Glass.kt`)
- Reusable frosted-glass effect component
- Blur + translucent fill + subtle glow + shadow
- Only applies blur effect on Android 12+ (API 31+)
- Configurable: corner radius, opacity, blur radius, glow intensity

**SplashScreen** (`ui/screens/SplashScreen.kt`)
- Initial screen with "Open Launcher" button
- Haptic feedback on interaction
- Navigates to home screen on button press

**Theme** (`ui/theme/Theme.kt`)
- Material 3 theming with custom color schemes
- Dark theme: deep blue/purple tones on near-black background
- Light theme: muted blues on light gray background

### Data Flow

1. **App Loading**: `HomeViewModel.load()` → `AppRepository.loadAllLaunchables()` → filters against pinned set
2. **Pinning**: User action → `HomeViewModel.pin()/unpin()` → `SettingsDataStore` → updates `pinned` Flow → UI recomposes
3. **Settings**: UI reads from `SettingsDataStore` Flows → User adjusts → writes back to DataStore → UI updates reactively
4. **Notifications**: `SlopeNotificationListener` service exposes active notifications as StateFlow (requires system permission grant)

### Key Technologies

- **Jetpack Compose**: Entire UI layer
- **Material 3**: Design system (custom color schemes)
- **Kotlin Coroutines + Flow**: Async operations and reactive state
- **DataStore**: Persistent settings storage
- **Navigation Compose**: Screen routing
- **Context Receivers**: Enabled via compiler flag (`-Xcontext-receivers`)

## Project Structure

```
app/src/main/java/com/slopeos/launcher/
├── MainActivity.kt                    # Main activity and navigation
├── data/
│   ├── AppEntry.kt                   # App data model
│   ├── AppRepository.kt              # Package manager queries
│   └── SettingsDataStore.kt          # Persistent settings
├── notifications/
│   └── SlopeNotificationListener.kt  # NotificationListenerService
├── ui/
│   ├── components/
│   │   └── Glass.kt                  # Frosted glass effect component
│   ├── screens/
│   │   ├── HomeScreen.kt             # Main launcher screen
│   │   └── SplashScreen.kt           # Initial splash
│   └── theme/
│       ├── Theme.kt                  # Material 3 theme definitions
│       └── Type.kt                   # Typography
├── util/
│   ├── DrawableUtils.kt              # Icon/drawable utilities
│   └── WallpaperUtils.kt             # Wallpaper blur utilities
└── viewmodel/
    └── HomeViewModel.kt              # Main launcher state
```

## Development Notes

### Android Version Support

- **Minimum SDK**: 28 (Android 9)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- Blur effects only work on Android 12+ (API 31+)

### Launcher Configuration

The app is configured as a HOME launcher in `AndroidManifest.xml`:
- `launchMode="singleTask"`
- `stateNotNeeded="true"`
- `excludeFromRecents="true"`
- Intent filter includes `android.intent.category.HOME`

### Permissions

- `BIND_NOTIFICATION_LISTENER_SERVICE`: Required for notification drawer feature
- User must manually grant Notification Access in system settings

### Platform Restrictions

Some quick settings (Wi-Fi, Bluetooth, Volume) open system panels via Settings Panel API because direct toggles require system-level permissions not available to third-party launchers.

### Kotlin Compiler Features

Project enables context receivers (`-Xcontext-receivers`) which may affect how extension functions are structured.
