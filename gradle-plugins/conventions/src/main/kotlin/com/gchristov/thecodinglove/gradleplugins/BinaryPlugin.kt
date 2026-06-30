package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class NodeBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(libs.findPlugin("thecodinglove-node-module").get().get().pluginId)
            plugins.apply("com.google.devtools.ksp")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js {
                    binaries.library()
                }
            }
            dependencies.add("kspJs", libs.findLibrary("kotlin-inject-compiler").get())
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
                js {
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