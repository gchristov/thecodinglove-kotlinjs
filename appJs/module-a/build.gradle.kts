plugins {
    id("javascript-library-plugin")
}

kotlin {
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