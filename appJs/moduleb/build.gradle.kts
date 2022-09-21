plugins {
    kotlin("js") version "1.7.20-RC"
}

kotlin {
    js(IR) {
        nodejs()
    }
}

dependencies {
    implementation("org.kodein.di:kodein-di:7.9.0")
    testImplementation(kotlin("test"))
}