plugins {
    id("module-plugin-2")
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