package com.gchristov.thecodinglove.gradleplugins

import com.gchristov.thecodinglove.gradleplugins.common.binaryDestination2
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class BackendBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("module-plugin")
            plugins.apply("dev.petuska.npm.publish")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    binaries.library()
                }
            }
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets.maybeCreate("commonMain").dependencies {
                    implementation(project(":common:network"))
                    implementation(project(":common:pubsub"))
                    implementation(project(":common:monitoring"))
                }
                sourceSets.maybeCreate("jsMain").dependencies {
                    // Ideally these would be linked from corresponding submodules but that is currently not supported out
                    // of the box or through the npm-publish plugin and causes "module not found" errors. As a workaround,
                    // all NPM dependencies will be listed at the top level here.
                    implementation(npm(Deps.Google.pubSub.name, Deps.Google.pubSub.version))
                    implementation(npm(Deps.Node.express.name, Deps.Node.express.version))
                    implementation(npm(Deps.Google.firebaseAdmin.name, Deps.Google.firebaseAdmin.version))
                }
                sourceSets.maybeCreate("jsTest").dependencies {
                    implementation(project(":common:network-testfixtures"))
                    implementation(project(":common:pubsub-testfixtures"))
                }
            }
            // Copy the output binaries to their final destination
            tasks.named("assemble") {
                doLast {
                    copy {
                        from(file(rootProject.layout.projectDirectory.file("credentials-gcp-app.json")))
                        into(binaryDestination2().get().dir("bin").asFile)
                    }
                    copy {
                        from(layout.buildDirectory.dir("packages/js").get().asFile)
                        into(binaryDestination2().get().dir("bin").asFile)
                    }
                    copy {
                        from(file(layout.projectDirectory.file("Dockerfile")))
                        into(binaryDestination2().get())
                    }
                }
            }
        }
    }
}