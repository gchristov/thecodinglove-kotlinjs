plugins {
    id("javascript-library-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.kodein.di:kodein-di:7.15.0")
            }
        }
    }
}