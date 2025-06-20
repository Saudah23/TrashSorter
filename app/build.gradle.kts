plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Plugin untuk Google Services (Firebase)
}

android {
    namespace = "com.kelompok4.trashsorter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kelompok4.trashsorter"
        minSdk = 25
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.3"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// ✅ Tambahkan di luar blok android
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    // UI dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Firestore Database
    implementation("com.google.firebase:firebase-firestore:24.10.0")

    // Enkripsi email & password
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Grafik chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Realtime Database
    implementation("com.google.firebase:firebase-database:20.3.0")
}
