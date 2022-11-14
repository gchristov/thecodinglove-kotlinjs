package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class JavascriptNodeLibraryPlugin : JavascriptNodePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.plugins.apply("dev.petuska.npm.publish")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                binaries.library()
            }
        }
    }
}

@Suppress("unused")
open class JavascriptNodePlugin : JavascriptPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                nodejs()
            }
        }
    }
}

@Suppress("unused")
class JavascriptBrowserExecutablePlugin : JavascriptPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                binaries.executable()
                browser {
                    commonWebpackConfig {
                        cssSupport.enabled = true
                    }
                }
            }
        }
    }
}

abstract class JavascriptPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(kotlin("test"))
            }
        }
        // TODO: Move this to a better location
        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(Deps.Ktor.client)
                implementation(Deps.Ktor.contentNegotiation)
                implementation(Deps.Ktor.serialisation)
                implementation(Deps.Ktor.logging)
                implementation(Deps.Ktor.logback)
            }
        }
    }
}