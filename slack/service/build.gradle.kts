plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.firebase)
                implementation(projects.slack.domain)
                implementation(projects.slack.adapter)
            }
        }
    }
}