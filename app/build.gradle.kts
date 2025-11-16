plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "1.9.23"
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}


android {
    namespace = "com.edureminder.easynotes"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.edureminder.easynotes"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        base.archivesName.set("Notepad-v${versionCode}")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            pickFirsts.add("META-INF/DEPENDENCIES")
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.compose.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.runtime)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.graphics)
//    implementation(platform(libs.androidx.compose.bom.v20240901)) // pick latest
    implementation(libs.ui)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.lottie.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)


    // ✅ FIX: Exclude the old, conflicting commonmark module
    implementation(libs.richeditor.compose) {
        exclude(group = "com.atlassian.commonmark", module = "commonmark")
    }
    implementation(libs.core) {
        exclude(group = "com.atlassian.commonmark", module = "commonmark")
    }


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.androidx.room.compiler)

    implementation("io.coil-kt:coil-compose:2.5.0")
    api(libs.coil)

    implementation("com.github.codergalib2005:compose-reorderable:1.0.0")


    // ---------- In App Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    implementation ("androidx.biometric:biometric:1.2.0-alpha05") // latest version
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Google drive authenticatio


    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0") // Use the latest version

    // Google APIs Client Library for Java (for Drive API)
    implementation("com.google.api-client:google-api-client-android:1.35.2") // Use the latest version
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0") // Use the latest version for Drive API v3
    // The 'google-oauth-client-jetty' dependency is typically for server-side or desktop
    // applications. For most Android cases, 'NetHttpTransport' used with 'GoogleAccountCredential'
    // in conjunction with 'google-auth-library-oauth2-http' is sufficient and recommended.
    // If you have a specific reason to use 'jetty' on Android, you can uncomment the line below.
    // implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0") // For OAuth 2.0

    val activity_version = "1.10.1"

    // Java language implementation // Kotlin
    implementation ("androidx.activity:activity-ktx:$activity_version")

    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    implementation ("androidx.hilt:hilt-work:1.1.0")

    // RevenueCat - Now enabled and working
    implementation(libs.revenuecat)
    implementation(libs.revenuecat.ui)

    implementation(libs.commonmark) // or latest version
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}