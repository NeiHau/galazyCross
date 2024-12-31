// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Gradle Plugin
    id("com.android.application") version "8.1.0" apply false
    // Kotlin Android Plugin
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    // Hilt Plugin
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Hilt の Gradle プラグインを追加
        classpath(libs.hilt.android.gradle.plugin)
        classpath("com.google.gms:google-services:4.4.2")
    }
}