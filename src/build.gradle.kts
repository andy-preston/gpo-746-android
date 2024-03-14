plugins {
    id("com.android.application").version("8.1.1").apply(false)
    id("com.android.library").version("8.1.1").apply(false)
    id("io.gitlab.arturbosch.detekt").version("1.23.0")
    // androidx.compose only supports up to 1.9.0
    kotlin("android").version("1.9.0").apply(false)
    kotlin("multiplatform").version("1.9.0").apply(false)
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
}

detekt {
    source.setFrom(
        "./",
        "./android/build.gradle.kts",
        "./android/src/main/kotlin/gpo746/android/",
        "./avr/",
        "./buildSrc/",
        "./buildSrc/src/main/kotlin/gpo746/",
        "./buildSrc/src/test/kotlin/gpo746/",
        "./shared/",
        "./shared/src/androidMain/kotlin/gpo746/",
        "./shared/src/androidUnitTest/kotlin/gpo746/",
        "./shared/src/commonMain/kotlin/gpo746/",
        "./shared/src/commonTest/kotlin/gpo746/",
        "./shared/src/linuxMain/kotlin/gpo746/"
    )
    config.setFrom("detekt.yml")
    buildUponDefaultConfig = true
    ignoredBuildTypes = listOf("release")
    ignoredFlavors = listOf("production")
    ignoredVariants = listOf("productionRelease")
}
