import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("base-node-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Kermit.logger)
                implementation(Deps.Kotlin.coroutinesCore)
                api(Deps.Kotlin.test)
            }
        }
    }
}