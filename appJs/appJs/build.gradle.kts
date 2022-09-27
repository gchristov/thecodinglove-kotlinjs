plugins {
    id("javascript-platform-plugin")
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(projects.moduleA)
            }
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}
