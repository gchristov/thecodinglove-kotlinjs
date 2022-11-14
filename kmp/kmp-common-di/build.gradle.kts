import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("javascript-node-platform-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Kodein.di)
            }
        }
    }
}