plugins {
    id("data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.commonFirebaseData)
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