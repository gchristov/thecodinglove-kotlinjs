plugins {
    id("data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.slackData)
            }
        }
    }
}