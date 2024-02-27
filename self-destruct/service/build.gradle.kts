import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.kotlin)
                implementation(Deps.Common.monitoring)
                implementation(Deps.Common.network)
                implementation(projects.adapter)
                implementation(projects.domain)
            }
        }
    }
}