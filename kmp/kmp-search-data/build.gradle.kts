plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kmpCommonFirebase)
                implementation(projects.kmpHtmlparse)
            }
        }
    }
}