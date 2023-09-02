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
            cinterops {
                val libusb by creating {
                    defFile(project.file("src/linuxMain/libusb/just-enough.def"))
                    packageName("libusb")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
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
    namespace = "andy_preston.gpo_746"
    compileSdk = 31
    defaultConfig {
        minSdk = 31
    }
}
