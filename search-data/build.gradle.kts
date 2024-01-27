plugins {
    id("data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.commonFirebaseData)
                implementation(projects.commonServiceData)
                implementation(projects.htmlParseData)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.commonServiceTestfixtures)
                implementation(projects.searchTestfixtures)
            }
        }
    }
}