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

fun sourceFile(project: String, file: String): File {
    return layout.projectDirectory.dir(
        "src"
    ).dir(
        project
    ).dir(
        "kotlin"
    ).dir(
        "gpo746"
    ).file(
        file
    ).asFile
}

tasks.register("createDataSources") {
    val toneDataSource = sourceFile("androidMain", "ToneData.kt")
    val ch340gConstantsSource = sourceFile("commonMain", "Ch340Constants.kt")
    doLast {
        if (!file(toneDataSource).exists()) {
            ToneGenerator(SAMPLE_FREQUENCY).fileOutput(toneDataSource)
        }
        if (!file(ch340gConstantsSource).exists()) {
            Ch340gConstants().fileOutput(ch340gConstantsSource)
        }
    }
}

tasks.named("compileKotlinLinuxX64") {
    dependsOn("createDataSources")
}
val dependentTasks = listOf(
    "compileCommonMainKotlinMetadata",
    "compileReleaseKotlinAndroid",
    "compileDebugKotlinAndroid"
)
tasks.whenTaskAdded {
    if (dependentTasks.contains(name)) {
        dependsOn("createDataSources")
    }
}
