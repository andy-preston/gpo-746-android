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
        "shared/src/commonMain/kotlin/",
        "shared/src/linuxMain/kotlin/",
        "shared/src/androidUnitTest/kotlin/",
        "shared/src/androidMain/kotlin/",
        "android/src/main/kotlin/",
        "buildSrc/src/test/kotlin/",
        "buildSrc/src/main/kotlin/"
    )
    config.setFrom("detekt.yml")
    buildUponDefaultConfig = true
    ignoredBuildTypes = listOf("release")
    ignoredFlavors = listOf("production")
    ignoredVariants = listOf("productionRelease")
}
