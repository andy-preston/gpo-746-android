plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "andyp.gpo746.android"
    compileSdk = 31
    defaultConfig {
        applicationId = "andyp.gpo746"
        minSdk = 31
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    lint {
        textReport = false
        xmlReport = false
        htmlReport = true
        // I'm specifically targeting MY phone, which runs Android 12
        // I need to understand how SDK versions work for old versions
        // before paying any attention to this.
        disable += "OldTargetApi"
        disable += "GradleDependency"
        // The App is in English only, if we need i18n down the line,
        // then it can be added.
        disable += "SetTextI18n"
        disable += "HardcodedText"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.compose.runtime:runtime:1.0.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.0.0")
}
