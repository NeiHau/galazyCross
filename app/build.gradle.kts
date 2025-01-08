import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    kotlin("kapt")
}

fun getEnvProperty(key: String): String? {
    val properties = Properties()
    val envFile = rootProject.file(".env")

    return try {
        if (envFile.exists()) {
            envFile.inputStream().use { stream ->
                properties.load(stream)
                properties.getProperty(key)
            }
        } else {
            System.getenv(key)
        }
    } catch (e: Exception) {
        null
    }
}

android {
    signingConfigs {
        create("release") {
            keyAlias = getEnvProperty("RELEASE_KEY_ALIAS") ?: ""
            keyPassword = getEnvProperty("RELEASE_KEY_PASSWORD") ?: ""
            storePassword = getEnvProperty("RELEASE_STORE_PASSWORD") ?: ""
            storeFile = file(getEnvProperty("RELEASE_STORE_FILE") ?: "")
        }
    }
    namespace = "com.hakutogames.galaxycross"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hakutogames.galaxycross"
        minSdk = 24
        targetSdk = 34
        versionCode = 23
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xcontext-receivers"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

detekt {
    ignoreFailures = false
    buildUponDefaultConfig = true
    config.setFrom(
        "$rootDir/config/detekt/detekt_code_smell_check_local.yml",
        "$rootDir/config/detekt/detekt_code_format_check.yml",
    )
    autoCorrect = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.firebase.functions.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ViewModel関連の依存関係
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Jetpack Compose用のViewModel拡張
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.navigation.compose)

    // Hilt Core
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    // （DataStore, Coroutine など他の依存も入れる）
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation(platform(libs.firebase.bom))

    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")

    // Linter
    detektPlugins(libs.detekt)

    // 3. Firebase以外の依存関係
    implementation(libs.billing.ktx)

    // Lottie
    implementation(libs.lottie.compose)

    // LiveData
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    kapt(libs.androidx.lifecycle.compiler)

    // ExoPlayer
    implementation(libs.exoplayer)

    implementation("com.google.android.gms:play-services-auth:21.3.0")
}