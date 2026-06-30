plugins {
    alias(libs.plugins.thecodinglove.node.binary)
}

apply(plugin = "com.google.devtools.ksp")

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.kotlin)
                implementation(libs.common.monitoring)
                implementation(libs.common.slack)
                implementation(libs.common.network)
                implementation(projects.adapter)
                implementation(projects.domain)
                implementation(libs.kotlin.inject.runtime)
            }
        }
    }
}

dependencies {
    add("kspJs", libs.kotlin.inject.compiler)
}