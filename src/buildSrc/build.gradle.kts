plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
}

sourceSets {
    val main by getting {
        dependencies {
            //put your multiplatform dependencies here
        }
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
