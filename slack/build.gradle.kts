plugins {
    id("backend-nested-service-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.slackData)
                implementation(projects.search)
            }
        }
    }
}