plugins {
    kotlin("js") version "1.7.10"
}

kotlin {
    js(IR) {
        nodejs()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":moduleb"))
            }
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}