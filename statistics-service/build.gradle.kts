import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("backend-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.commonFirebaseData)
                implementation(projects.htmlParseData)
                implementation(projects.monitoringData)
                implementation(projects.searchData)
                implementation(projects.statisticsData)
                implementation(projects.slackData)
            }
        }
        val jsMain by getting {
            dependencies {
                // Ideally these would be linked from corresponding submodules but that is currently
                // not supported out of the box or through the npm-publish plugin and causes "module
                // not found" errors. As a workaround, all NPM dependencies will be listed here,
                // making them available to all submodules.
                implementation(npm(Deps.Google.pubSub.name, Deps.Google.pubSub.version))
                implementation(npm(Deps.Google.firebaseAdmin.name, Deps.Google.firebaseAdmin.version))
                implementation(npm(Deps.Node.express.name, Deps.Node.express.version))
                implementation(npm(Deps.Node.jsJoda.name, Deps.Node.jsJoda.version))
            }
        }
    }
}