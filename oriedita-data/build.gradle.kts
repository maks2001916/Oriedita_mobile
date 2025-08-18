plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}
android {
    namespace = "com.example.oriedita_data"
    compileSdk = 35
        defaultConfig {
            minSdk = 24
            targetSdk = 35
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.jsonassert)
    testImplementation(libs.snapshot.testing)
    // Timber for Android logging (replaces tinylog and slf4j)
    implementation(libs.timber)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.hilt.android)
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Hilt for dependency injection (replaces CDI)
    implementation("com.google.dagger:hilt-core:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Jackson dependencies for JSON processing
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.datatype.jdk8)
    implementation(libs.jackson.datatype.jsr310)
    
    // FOLD library for .fold file format support
    implementation("io.github.oriedita:fold:1.1.0")

    implementation(project(":oriedita-common"))
    implementation(project(":origami"))
}