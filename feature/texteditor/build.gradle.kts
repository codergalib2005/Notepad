plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.feature.edureminder.texteditor"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /**
     * Added
     */
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.runtime:runtime:1.7.0")
    implementation("androidx.compose.foundation:foundation:1.7.0")
    implementation("androidx.compose.animation:animation:1.7.0")
    implementation("androidx.compose.ui:ui-util:1.7.0")
    implementation(libs.androidx.compose.ui)
    implementation(platform("androidx.compose:compose-bom:2025.10.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")        // Material 2

    // HTML parsing library
    implementation(libs.ksoup.html)
    implementation(libs.ksoup.entities)
//    val version = "0.6.0"
//
// For parsing HTML
//    implementation("com.mohamedrejeb.ksoup:ksoup-html:$version")
//
// Only for encoding and decoding HTML entities
//    implementation("com.mohamedrejeb.ksoup:ksoup-entities:$version")

    // Markdown parsing library
    implementation(libs.jetbrains.markdown)
}