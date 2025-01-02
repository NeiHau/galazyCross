import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
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
    namespace = "com.example.galaxycross"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hakutogames.galaxycross"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.0"

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
    }
    buildFeatures {
        compose = true
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

    implementation("androidx.navigation:navigation-compose:2.8.5")

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

    // 3. Firebase以外の依存関係
    implementation(libs.billing.ktx)

    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // Lifecycle components
    val lifecycle_version = "2.7.0"

    // LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
}