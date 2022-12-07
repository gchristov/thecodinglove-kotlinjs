plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.searchData)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(projects.searchTestfixtures)
            }
        }
    }
}