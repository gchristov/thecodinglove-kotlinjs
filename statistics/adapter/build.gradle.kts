plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.search.proto)
                implementation(projects.slack.proto)
                implementation(projects.statistics.domain)
                implementation(projects.statistics.proto)
            }
        }
    }
}