plugins {
    id("kmp-module-plugin")
    // TODO: This shouldn't be needed here but is used for serialisation (move to slack-data)
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.commonFirebase)
                implementation(projects.slackData)
                implementation(projects.search)
            }
        }
    }
}