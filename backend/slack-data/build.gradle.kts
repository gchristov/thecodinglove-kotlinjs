plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.commonServiceData)
                implementation(projects.searchData)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.slackTestfixtures)
            }
        }
    }
}