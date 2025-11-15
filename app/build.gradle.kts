plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.2.0" // match Kotlin version
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}


android {
    namespace = "com.example.notepad"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.notepad"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation(libs.androidx.material.icons.extended)

    implementation ("androidx.work:work-runtime-ktx:2.9.0")
    implementation ("androidx.hilt:hilt-work:1.1.0")

    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.animation:animation:1.9.4")

    implementation("com.exyte:animated-navigation-bar:1.0.0")
    implementation("com.github.commandiron:WheelPickerCompose:1.1.11")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    implementation(libs.androidx.runtime.livedata)
    implementation ("io.github.ehsannarmani:compose-charts:0.1.11")


    implementation("com.github.codergalib2005:compose-reorderable:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.1.7")
    implementation("androidx.datastore:datastore-preferences:1.1.7")


    implementation(libs.androidx.core.splashscreen)
    // ---------- In App Update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
}
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}