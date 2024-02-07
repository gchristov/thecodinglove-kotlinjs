plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.statistics.domain)
                // TODO: Delete this once the services have been migrated to hexagonal architecture
                implementation(projects.searchData)
            }
        }
    }
}