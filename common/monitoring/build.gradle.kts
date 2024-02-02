plugins {
    id("module-plugin-2")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // TODO: Remove once Slack is migrated to microservice
                api(projects.slackData)
            }
        }
    }
}