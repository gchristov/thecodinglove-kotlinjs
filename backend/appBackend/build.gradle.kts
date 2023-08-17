import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("backend-executable-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.search)
                implementation(projects.slack)
            }
        }
        val jsMain by getting {
            dependencies {
                // Ideally these would be linked from corresponding submodules but that is currently
                // not supported out of the box or through the npm-publish plugin and causes "module
                // not found" errors. As a workaround, all NPM dependencies will be listed here,
                // making them available to all submodules.
                implementation(npm(Deps.Google.pubSub.name, Deps.Google.pubSub.version))
                implementation(npm(Deps.Node.htmlParser.name, Deps.Node.htmlParser.version))
                implementation(npm(Deps.Node.express.name, Deps.Node.express.version))
                implementation(npm(Deps.Node.jsJoda.name, Deps.Node.jsJoda.version))
            }
        }
    }
}

// Copy the output binaries to their final destination
tasks.named("assemble") {
    doLast {
        copy {
            from(file("$buildDir/productionLibrary"))
            into(file("$rootDir/build/bin"))
        }
        copy {
            from(file("$rootDir/local-credentials-pubsub.json"))
            into(file("$rootDir/build/bin"))
        }
    }
}