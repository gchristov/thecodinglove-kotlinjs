plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.adapter)
                implementation(projects.domain)
            }
        }
    }
}