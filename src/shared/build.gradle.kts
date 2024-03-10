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

tasks.register("createDataSource") {
    val toneDataSource = "src/androidMain/kotlin/gpo746/ToneData.kt"
    val ch340gConstantsSource = "src/commonMain/kotlin/gpo746/Ch340Constants.kt"
    doLast {
        if (!file(toneDataSource).exists()) {
            ToneGenerator(SAMPLE_FREQUENCY).fileOutput(
                file(
                    layout.projectDirectory.file(
                        toneDataSource
                    )
                )
            )
        }
        if (!file(ch340gConstantsSource).exists()) {
            Ch340gConstants().fileOutput(
                file(
                    layout.projectDirectory.file(
                        ch340gConstantsSource
                    )
                )
            )
        }
    }
}

tasks.named("compileKotlinLinuxX64") {
    dependsOn("createDataSource")
}
val dependentTasks = listOf(
    "compileCommonMainKotlinMetadata",
    "compileReleaseKotlinAndroid",
    "compileDebugKotlinAndroid"
)
tasks.whenTaskAdded {
    if (dependentTasks.contains(name)) {
        dependsOn("createDataSource")
    }
}
