plugins {
    alias(libs.plugins.thecodinglove.node.binary)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.kotlin)
                implementation(libs.common.monitoring)
                implementation(libs.common.network)
                implementation(projects.adapter)
                implementation(projects.domain)
            }
        }
    }
}