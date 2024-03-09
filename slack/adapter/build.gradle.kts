import com.gchristov.thecodinglove.gradleplugins.Deps
import com.gchristov.thecodinglove.gradleplugins.envSecret

plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.analytics)
                implementation(Deps.Common.kotlin)
                implementation(Deps.Common.network)
                implementation(Deps.Common.pubsub)
                implementation(Deps.Common.firebase)
                implementation(projects.domain)
            }
        }
    }
}