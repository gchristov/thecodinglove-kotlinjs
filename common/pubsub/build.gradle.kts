plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.common.network)
            }
        }
    }
}