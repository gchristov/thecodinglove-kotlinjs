plugins {
    id("module-plugin-2")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.adapter)
                implementation(projects.statistics.domain)
                // TODO: Delete this once the services have been migrated to hexagonal architecture
                implementation(projects.slackData)
                implementation(projects.searchData)
            }
        }
    }
}