package com.gchristov.thecodinglove.gradleplugins

import com.gchristov.thecodinglove.gradleplugins.common.binaryDestination2
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FrontendBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("base-browser-plugin")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    binaries.executable()
                }
            }
            // Copy the output binaries to their final destination
            tasks.named("assemble") {
                doLast {
                    copy {
                        from(layout.buildDirectory.dir("dist/js/productionExecutable").get().asFile)
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