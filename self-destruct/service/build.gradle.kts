plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.selfDestruct.adapter)
                implementation(projects.selfDestruct.domain)
            }
        }
    }
}