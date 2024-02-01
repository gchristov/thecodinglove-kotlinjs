plugins {
    id("module-plugin-2")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.adapter)
            }
        }
    }
}