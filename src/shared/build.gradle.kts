plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val sourceDirectory = layout.projectDirectory.dir("src")

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    linuxX64 {
        binaries {
            executable()
        }
        compilations.getByName("main") {

            @Suppress("UnusedPrivateProperty")
            val libUsb by cinterops.creating {
                defFile(sourceDirectory.dir("linuxMain").file("libusb.def"))
                packageName("libusb")
            }
        }
    }
    sourceSets {

        @Suppress("UnusedPrivateProperty")
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }

        @Suppress("UnusedPrivateProperty")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "andy_preston.gpo746"
    compileSdk = 31
    defaultConfig {
        minSdk = 31
    }
}

tasks.register<Ch340gConstants>("prepareConstants") {
    directory(sourceDirectory.dir("commonMain").dir("kotlin").dir("gpo746"))
    file("Ch340Constants.kt")
}

tasks.register<ToneGenerator>("prepareTones") {
    directory(sourceDirectory.dir("androidMain").dir("kotlin").dir("gpo746"))
    file("ToneSamples.kt")
}

tasks.named("compileKotlinLinuxX64") {
    dependsOn("prepareConstants")
}

val dependentTasks = listOf(
    "compileCommonMainKotlinMetadata",
    "compileReleaseKotlinAndroid",
    "compileDebugKotlinAndroid"
)
tasks.whenTaskAdded {
    if (dependentTasks.contains(name)) {
        dependsOn("prepareConstants")
        dependsOn("prepareTones")
    }
}
