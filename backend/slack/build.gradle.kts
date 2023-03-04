plugins {
    id("backend-service-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.slackData)
                implementation(projects.search)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(projects.slackTestfixtures)
            }
        }
    }
}