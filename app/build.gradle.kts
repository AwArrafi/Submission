plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.submission"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.submission"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit untuk API calls
    implementation (libs.retrofit)

    // Gson converter untuk Retrofit
    implementation (libs.converter.gson)

    // Kotlin Coroutines untuk asynchronous programming
    implementation (libs.kotlinx.coroutines.android)

    // Lifecycle for coroutine scope in Activity/Fragment
    implementation (libs.androidx.lifecycle.runtime.ktx)

    // OkHttp untuk log network requests (optional, but useful for debugging)
    implementation (libs.logging.interceptor)

    // Untuk bekerja dengan Material Components (jika belum ada)
    implementation (libs.material)

    implementation (libs.androidx.datastore.preferences)


    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.savedstate)


    implementation (libs.hilt.android)
    implementation (libs.androidx.hilt.lifecycle.viewmodel)

    implementation (libs.glide)
}