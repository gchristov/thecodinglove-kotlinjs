plugins {
    alias(libs.plugins.thecodinglove.node.module)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.kotlin)
                implementation(libs.common.network)
                implementation(projects.domain)
            }
        }
    }
}