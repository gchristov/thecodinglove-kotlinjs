import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("backend-binary-plugin-2")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.slacknew.domain)
                implementation(projects.slacknew.adapter)
                // TODO: Update these once the services have been migrated to hexagonal architecture
                implementation(projects.htmlParseData)
                implementation(projects.searchData)
            }
        }
        val jsMain by getting {
            dependencies {
                // Ideally these would be linked from corresponding submodules but that is currently not supported out
                // of the box or through the npm-publish plugin and causes "module not found" errors. As a workaround,
                // all NPM dependencies will be listed at the top level here.
                implementation(npm(Deps.Node.htmlParser.name, Deps.Node.htmlParser.version))
            }
        }
    }
}