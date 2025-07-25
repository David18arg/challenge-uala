plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.hilt)
    alias(libs.plugins.kotlin.ksp)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.david.cityapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.david.cityapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Weather API Key
        buildConfigField("String", "WEATHER_API_KEY", "\"c02b1d22d453ef36d5705ff26a89c8ca\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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

    // Kotlin and Coroutines
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.stdlib)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Pagging
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.compose.android)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.ui.test.junit4.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Osmdroid maps
    implementation(libs.osm.droid.android)
    implementation(libs.osm.droid.mapsforge)

    // Coil
    implementation(libs.coil.compose)

    // lottie
    implementation(libs.lottie.compose)

    // Unit testing
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent.jvm)

    // AndroidX Test
    testImplementation(libs.core.ktx)
    testImplementation(libs.androidx.runner)
    testImplementation(libs.androidx.rules)

    testImplementation(libs.kotlinx.coroutines.test)

    // Mockito
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)

    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.turbine)

    // Android instrumented tests
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}