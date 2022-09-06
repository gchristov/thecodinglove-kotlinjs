import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js") version "1.7.10"
}

group = "me.georgi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    js {
        binaries.executable()
        nodejs {

        }
    }
}

tasks.withType<Kotlin2JsCompile>() {
    kotlinOptions {
        moduleKind = "commonjs"
        outputFile = "$projectDir/functions/index.js"
        sourceMap = true
    }
}