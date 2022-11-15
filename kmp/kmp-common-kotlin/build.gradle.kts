import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("javascript-node-target-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Kotlin.coroutinesCore)
            }
        }
    }
}