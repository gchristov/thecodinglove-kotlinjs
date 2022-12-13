plugins {
    id("backend-service-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.slackData)
            }
        }
    }
}