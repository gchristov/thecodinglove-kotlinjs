plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.search.adapter)
                implementation(projects.search.domain)
            }
        }
    }
}