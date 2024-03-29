import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.analytics)
                implementation(Deps.Common.kotlin)
                implementation(Deps.Common.network)
                implementation(Deps.Common.pubsub)
                implementation(Deps.Common.firebase)
                implementation(projects.domain)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Common.test)
                implementation(Deps.Common.analyticsTestFixtures)
                implementation(Deps.Common.networkTestFixtures)
                implementation(Deps.Common.pubsubTestFixtures)
                implementation(projects.testFixtures)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(Deps.Node.htmlParser.name, Deps.Node.htmlParser.version))
            }
        }
    }
}