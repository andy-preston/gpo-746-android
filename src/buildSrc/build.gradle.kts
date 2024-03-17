plugins {
    kotlin("jvm").version("1.9.0")
}

sourceSets {

    @Suppress("UnusedPrivateProperty")
    val main by getting {
        dependencies {
            implementation(gradleApi())
        }
    }

    @Suppress("UnusedPrivateProperty")
    val test by getting {
        dependencies {
            implementation(kotlin("test"))
        }
    }
}

repositories {
    mavenCentral()
}
