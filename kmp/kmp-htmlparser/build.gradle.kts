import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("javascript-node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(npm(Deps.Node.htmlParser.name, Deps.Node.htmlParser.version))
            }
        }
    }
}