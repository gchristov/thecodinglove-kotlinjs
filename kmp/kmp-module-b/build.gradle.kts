plugins {
    kotlin("js") version "1.7.20-RC"
}

kotlin {
    js(IR) {
        nodejs()
    }
}

dependencies {
    testImplementation(kotlin("test"))
}