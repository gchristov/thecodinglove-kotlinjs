plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:firebase")
                implementation(projects.domain)
                implementation(projects.adapter)
            }
        }
    }
}