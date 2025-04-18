plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // id("com.google.gms.google-services") version "4.4.0"
    // id("com.google.firebase.crashlytics") version "2.9.9"
    id("org.jetbrains.dokka") version "1.9.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

android {
    namespace = "com.example.trimly"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.trimly"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "LICENSE", "\"${project.file("../LICENSE").readText().replace("\n", "\\n")}\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Firebase
    // implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    // implementation("com.google.firebase:firebase-analytics-ktx")
    // implementation("com.google.firebase:firebase-crashlytics-ktx")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
    dokkaSourceSets {
        named("main") {
            moduleName.set("Trimly")
            includes.from("README.md")
            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl.set(uri("https://github.com/yourusername/trimly/tree/main/app/src/main/java").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}

ktlint {
    version.set("1.0.1")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
}