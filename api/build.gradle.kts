plugins {
    id("com.android.library")
}

android {
    namespace = "io.github.libxposed"
    compileSdk = 33
    buildToolsVersion = "33.0.1"

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        consumerProguardFiles("proguard-rules.pro")
    }

    buildFeatures {
        androidResources = false
        buildConfig = false
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    compileOnly("androidx.annotation:annotation:1.5.0")
}
