import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.kotlin)
            }
        }
    }
}