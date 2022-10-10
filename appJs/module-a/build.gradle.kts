plugins {
    id("javascript-library-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kmpModuleB)
            }
        }
    }
}