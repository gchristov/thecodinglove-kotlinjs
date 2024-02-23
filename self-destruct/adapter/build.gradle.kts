plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:network")
                implementation(projects.domain)
            }
        }
    }
}