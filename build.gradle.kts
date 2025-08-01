// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.24"
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.0.20" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" apply false
}