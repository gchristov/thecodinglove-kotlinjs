import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("javascript-node-executable-plugin")
    // TODO: This shouldn't be needed here
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // TODO: Use feature modules here when ready
                implementation(projects.kmpCommonFirebase)
                implementation(projects.kmpHtmlparser)
                implementation(projects.moduleA)
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