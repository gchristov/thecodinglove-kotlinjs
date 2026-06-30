plugins {
    alias(libs.plugins.thecodinglove.node.binary)
}

apply(plugin = "com.google.devtools.ksp")

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.analytics)
                implementation(libs.common.kotlin)
                implementation(libs.common.monitoring)
                implementation(libs.common.slack)
                implementation(libs.common.network)
                implementation(libs.common.pubsub)
                implementation(libs.common.firebase)
                implementation(projects.domain)
                implementation(projects.adapter)
            }
        }
    }
}

dependencies {
    add("kspJs", libs.kotlin.inject.compiler)
}