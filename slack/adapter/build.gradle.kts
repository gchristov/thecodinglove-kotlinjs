plugins {
    alias(libs.plugins.thecodinglove.node.module)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.analytics)
                implementation(libs.common.kotlin)
                implementation(libs.common.network)
                implementation(libs.common.pubsub)
                implementation(libs.common.firebase)
                implementation(projects.domain)
            }
        }
    }
}