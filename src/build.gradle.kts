plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.1.1").apply(false)
    id("com.android.library").version("8.1.1").apply(false)
    // linuxArm32Hfp and linuxMips32 will be removed in 1.9.20
    // androidx.compose only supports up to 1.9.0
    kotlin("android").version("1.9.0").apply(false)
    kotlin("multiplatform").version("1.9.0").apply(false)
}
