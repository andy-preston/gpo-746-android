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

    listOf(linuxX64(), linuxArm32Hfp()).forEach {
        it.binaries {
            executable()
        }
        it.compilations.getByName("main") {
            val libUsb by cinterops.creating {
                defFile(project.file("src/linuxMain/libusb/just-enough.def"))
                packageName("libusb")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "andy_preston.gpo_746"
    compileSdk = 31
    defaultConfig {
        minSdk = 31
    }
}

val commonDirectory = layout.projectDirectory.dir("src/commonMain/kotlin/gpo_746")
tasks.register<Copy>("ch340gConstants") {
    from(commonDirectory)
    into(commonDirectory)
    include("*.kt_template")
    rename("(.*)_template", "$1")
    filter(
        ReplaceTokens::class,
        "tokens" to Ch340gConstants().map()
    )
}
tasks.named("compileKotlinLinuxX64") {
    dependsOn("ch340gConstants")
}
tasks.named("compileKotlinLinuxArm32Hfp") {
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
