# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to the flags specified
# in the /tools/proguard/proguard-android.txt file that is
# supplied with the Android SDK.

# Keep Jetpack Compose generated classes
-keep class * extends androidx.compose.runtime.ComposerKt { *; }
-keep class androidx.compose.** { *; }
-dontwarn kotlinx.coroutines.**
