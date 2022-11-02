buildscript {
    val kotlin_version = "1.6.21"
    repositories {
        mavenCentral()
        maven(url = "https://maven.google.com/")
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

