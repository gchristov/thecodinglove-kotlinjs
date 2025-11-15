package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class NodeBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(libs.findPlugin("thecodinglove-node-module").get().get().pluginId)
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    binaries.library()
                }
            }
            // Copy the output binaries to their final destination
            tasks.named("assemble") {
                doLast {
                    copy {
                        from(file(rootProject.layout.projectDirectory.file("../credentials-gcp-app.json")))
                        into("${binaryRootDirectory()}/productionExecutable")
                    }
                    copy {
                        from(file(layout.projectDirectory.file("Dockerfile")))
                        into(binaryRootDirectory())
                    }
                }
            }
        }
    }
}

class BrowserBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(libs.findPlugin("thecodinglove-base-browser").get().get().pluginId)
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    binaries.executable()
                }
            }
            // Copy the output binaries to their final destination
            tasks.named("assemble") {
                doLast {
                    copy {
                        from(file(layout.projectDirectory.file("Dockerfile")))
                        into(binaryRootDirectory())
                    }
                }
            }
        }
    }
}