plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.oriedita_common"
    compileSdk = 35

    defaultConfig {
        namespace = "com.example.oriedita_common"
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
    
    lint {
        disable += "MissingTranslation"
        abortOnError = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.runtime.android)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":origami"))
    
    // Hilt for dependency injection (replaces CDI)
    implementation("com.google.dagger:hilt-core:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Jackson dependencies for JSON processing
    implementation(libs.jackson.annotations)

}