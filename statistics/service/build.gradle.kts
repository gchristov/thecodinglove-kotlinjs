plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.kotlin)
                implementation(libs.common.monitoring)
                implementation(libs.common.network)
                implementation(projects.domain)
                implementation(projects.adapter)
            }
        }
    }
}