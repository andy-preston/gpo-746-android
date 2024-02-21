import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

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

    linuxX64() {
        binaries {
            executable()
        }
        compilations.getByName("main") {
            val libUsb by cinterops.creating {
                defFile(project.file("src/linuxMain/libusb.def"))
                packageName("libusb")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }

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

tasks.register("ch340gConstants") {
    doLast {
        Ch340gConstants().fileOutput(file(layout.projectDirectory.file(
            "src/commonMain/kotlin/gpo746/Ch340Constants.kt"
        )))
    }
}

tasks.named("compileKotlinLinuxX64") {
    dependsOn("ch340gConstants")
}
val dependentTasks = listOf(
    "compileCommonMainKotlinMetadata",
    "compileReleaseKotlinAndroid",
    "compileDebugKotlinAndroid"
)
tasks.whenTaskAdded {
    if (dependentTasks.contains(name)) {
        dependsOn("ch340gConstants")
    }
}
