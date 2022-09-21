plugins {
    kotlin("js") version "1.7.20-RC"
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs()
    }
}

dependencies {
    implementation(projects.modulea)
    testImplementation(kotlin("test"))
}
