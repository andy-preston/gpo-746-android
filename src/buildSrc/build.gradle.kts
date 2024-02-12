plugins {
    kotlin("jvm").version("1.9.0")
}

sourceSets {
    val main by getting {
    }
    val test by getting {
        dependencies {
            implementation(kotlin("test"))
        }
    }
}

repositories {
    mavenCentral()
}
