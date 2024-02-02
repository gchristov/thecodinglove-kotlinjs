plugins {
    id("module-plugin-2")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.common.firebase)
                implementation(projects.htmlParseData)
                implementation(projects.monitoringData)
                implementation(projects.statistics.domain)
                implementation(projects.statistics.adapter)
                // TODO: Delete this once the services have been migrated to hexagonal architecture
                implementation(projects.searchData)
                implementation(projects.slackData)
            }
        }
    }
}