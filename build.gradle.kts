// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Versions are applied in the module to keep this root minimal
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
