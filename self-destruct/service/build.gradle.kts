plugins {
    id("backend-binary-plugin-2")
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