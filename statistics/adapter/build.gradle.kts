plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.statistics.domain)
                implementation(projects.search.proto)
            }
        }
    }
}