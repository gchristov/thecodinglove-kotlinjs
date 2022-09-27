plugins {
    id("javascript-application-plugin")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(projects.moduleA)
            }
        }
    }
}