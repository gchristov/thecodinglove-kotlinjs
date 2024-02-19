plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.statistics.domain)
                implementation(projects.statistics.adapter)
            }
        }
    }
}