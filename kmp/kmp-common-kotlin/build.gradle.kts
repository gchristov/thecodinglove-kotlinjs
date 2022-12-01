import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("kmp-platform-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Kotlin.coroutinesCore)
                api(Deps.Uuid.uuid)
                api(Deps.Arrow.core)
            }
        }
    }
}