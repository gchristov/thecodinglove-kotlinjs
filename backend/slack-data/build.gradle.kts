plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.commonServiceData)
                implementation(projects.searchData)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(projects.slackTestfixtures)
            }
        }
    }
}