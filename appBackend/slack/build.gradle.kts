plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.slackData)
                // TODO: This should include the data module, rather than the feature module
                implementation(projects.search)
            }
        }
    }
}