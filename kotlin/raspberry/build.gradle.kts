plugins {
    kotlin("multiplatform")
}

kotlin {
    listOf(linuxX64(), linuxArm32Hfp()).forEach {
        it.binaries {
            executable()
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.3"
    distributionType = Wrapper.DistributionType.BIN
}
