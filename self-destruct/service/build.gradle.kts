plugins {
    id("backend-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.slack.proto)
                implementation(projects.selfDestruct.adapter)
                implementation(projects.selfDestruct.domain)
            }
        }
    }
}