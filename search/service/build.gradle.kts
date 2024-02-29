import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("node-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.kotlin)
                implementation(Deps.Common.firebase)
                implementation(Deps.Common.monitoring)
                implementation(Deps.Common.network)
                implementation(Deps.Common.pubsub)
                implementation(projects.domain)
                implementation(projects.adapter)
            }
        }
    }
}