plugins {
    id("javascript-node-module-plugin")
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