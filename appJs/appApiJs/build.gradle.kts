plugins {
    id("javascript-node-library-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // TODO: These should not be directly accessed
                implementation(projects.kmpCommonFirebase)
                implementation(projects.kmpCommonNetwork)
                implementation(projects.moduleA)
                // Needed to get the Firebase deployment to work
                implementation(npm("firebase", "9.10.0"))
                implementation(npm("firebase-admin", "11.0.1"))
                implementation(npm("firebase-functions", "3.24.0"))
            }
        }
    }
}