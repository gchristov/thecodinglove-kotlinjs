plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.common.network)
            }
        }
    }
}