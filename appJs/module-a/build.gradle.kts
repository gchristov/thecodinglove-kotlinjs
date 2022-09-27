plugins {
    id("kmp-platform-plugin")
}

kotlin {
    js(IR) {
        nodejs()
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(projects.kmpModuleB)
            }
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}