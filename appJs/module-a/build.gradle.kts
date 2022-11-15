plugins {
    id("javascript-node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kmpModuleB)
                implementation(npm("express", "4.18.2"))
            }
        }
    }
}