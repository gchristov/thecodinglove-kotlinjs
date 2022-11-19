import com.gchristov.thecodinglove.kmpgradleplugins.Deps

plugins {
    id("kmp-platform-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Kodein.di)
            }
        }
    }
}