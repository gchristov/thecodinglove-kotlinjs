plugins {
    alias(libs.plugins.thecodinglove.node.binary)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.analytics)
                implementation(libs.common.kotlin)
                implementation(libs.common.firebase)
                implementation(libs.common.monitoring)
                implementation(libs.common.network)
                implementation(libs.common.pubsub)
                implementation(projects.domain)
                implementation(projects.adapter)
            }
        }
    }
}