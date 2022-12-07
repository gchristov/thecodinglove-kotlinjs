plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.htmlparseData)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.htmlparseTestfixtures)
            }
        }
    }
}