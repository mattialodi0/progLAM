plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.proglam"
    compileSdk = 34

    dataBinding {
        android.buildFeatures.dataBinding = true
    }

    defaultConfig {
        applicationId = "com.example.proglam"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Room components
    implementation("androidx.room:room-runtime:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
    implementation("androidx.room:room-ktx:2.2.5")
    androidTestImplementation("androidx.room:room-testing:2.2.5")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    // Workers
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // service
    implementation("androidx.lifecycle:lifecycle-service:2.8.1")

    // charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")

    // calendar
    implementation("com.applandeo:material-calendar-view:1.9.2")

    // location
    implementation("com.google.gms:google-services:4.4.2")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // JSON
    implementation("com.google.code.gson:gson:2.11.0")
}