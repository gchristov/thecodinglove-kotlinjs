import com.gchristov.thecodinglove.gradleplugins.Deps
import com.gchristov.thecodinglove.gradleplugins.Deps.Common.kotlin
import com.gchristov.thecodinglove.gradleplugins.envSecret

plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.kotlin)
                implementation(Deps.Common.network)
                implementation(projects.domain)
            }
        }
    }
}