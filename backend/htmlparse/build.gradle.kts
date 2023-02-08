plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.htmlparseData)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(projects.htmlparseTestfixtures)
            }
        }
    }
}