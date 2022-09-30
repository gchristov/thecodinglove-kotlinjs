plugins {
    id("javascript-application-plugin")
}

kotlin {
    js(IR) {
        compilations["main"].packageJson {
            customField(
                "dependencies", mapOf(
                    "firebase" to "9.10.0",
                    "firebase-admin" to "11.0.1",
                    "firebase-functions" to "3.24.0",
                )
            )
        }
    }
    sourceSets {
        val main by getting {
            dependencies {
                implementation(projects.moduleA)
            }
        }
    }
}