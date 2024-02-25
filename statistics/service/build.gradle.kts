plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:kotlin")
                implementation("com.gchristov.thecodinglove.common:monitoring")
                implementation("com.gchristov.thecodinglove.common:network")
                implementation(projects.domain)
                implementation(projects.adapter)
            }
        }
    }
}