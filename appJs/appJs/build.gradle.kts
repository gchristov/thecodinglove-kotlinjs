plugins {
    kotlin("js") version "1.7.10"
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":modulea"))
            }
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}
