plugins {
    id("backend-service-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.slackData)
            }
        }
    }
}