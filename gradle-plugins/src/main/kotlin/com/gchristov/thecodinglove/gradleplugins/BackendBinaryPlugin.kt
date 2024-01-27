package com.gchristov.thecodinglove.gradleplugins

import com.gchristov.thecodinglove.gradleplugins.common.binaryDestination
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class BackendBinaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("base-plugin")
            plugins.apply("dev.petuska.npm.publish")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    binaries.library()
                }
            }
            // Copy the output binaries to their final destination
            tasks.named("assemble") {
                doLast {
                    copy {
                        from(layout.buildDirectory.dir("packages/js").get().asFile)
                        into(binaryDestination().get().dir("bin").asFile)
                    }
                    copy {
                        from(file(layout.projectDirectory.file("Dockerfile")))
                        into(binaryDestination().get())
                    }
                }
            }
        }
    }
}

/*
 * Ideally all microservices should be individual binaries deployed separately, using the above plugin. However, the
 * project still has some monolith parts where different service modules are exposed together. To support this, we need
 * this separate plugin to ensure they aren't exposed individually and instead part of whatever binary they are linked
 * to.
 * TODO: Consider exposing the modules using this plugin as individual microservices
 */
@Suppress("unused")
class BackendNestedServicePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("data-plugin")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets.maybeCreate("jsMain").dependencies {
                    api(project(":common-service"))
                }
                sourceSets.maybeCreate("jsTest").dependencies {
                    api(project(":common-service-testfixtures"))
                }
            }
        }
    }
}