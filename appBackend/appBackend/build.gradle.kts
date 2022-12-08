import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("backend-executable-plugin")
    // TODO: This shouldn't be needed here but is used for examples
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                // TODO: Use feature modules here when ready. These are only used for examples
                implementation(projects.kmpCommonKotlin)
                implementation(projects.search)
                implementation(projects.slack)
                // Ideally these would be linked from corresponding submodules but that is currently
                // not supported out of the box or through the npm-publish plugin and causes "module
                // not found" errors. As a workaround, all NPM dependencies will be listed here,
                // making them available to all submodules.
                implementation(npm(Deps.Firebase.firebase.name, Deps.Firebase.firebase.version))
                implementation(npm(Deps.Firebase.admin.name, Deps.Firebase.admin.version))
                implementation(npm(Deps.Firebase.functions.name, Deps.Firebase.functions.version))
                implementation(npm(Deps.Node.htmlParser.name, Deps.Node.htmlParser.version))
                implementation(npm(Deps.Node.express.name, Deps.Node.express.version))
            }
        }
    }
}