plugins {
    id("javascript-node-library-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(npm("firebase", "9.10.0"))
                implementation(npm("firebase-admin", "11.0.1"))
                implementation(npm("firebase-functions", "3.24.0"))
                implementation(projects.kmpCommonFirebase)
                implementation(projects.moduleA)
            }
        }
    }
}