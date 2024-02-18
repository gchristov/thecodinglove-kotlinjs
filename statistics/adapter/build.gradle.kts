plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.search.proto)
                implementation(projects.statistics.domain)
            }
        }
    }
}