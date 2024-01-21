plugins {
    id("backend-service-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.slackData)
                implementation(projects.searchData)
            }
        }
    }
}