package com.gchristov.thecodinglove.gradleplugins.js

import com.gchristov.thecodinglove.gradleplugins.Deps
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class JavascriptNodeLibraryPlugin : JavascriptNodeModulePlugin() {
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
class JavascriptBrowserExecutablePlugin : JavascriptBrowserTargetPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                binaries.executable()
            }
        }
    }
}

@Suppress("unused")
open class JavascriptNodeModulePlugin : JavascriptNodeTargetPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(kotlin("test"))
                implementation(project(":kmp-common-di"))
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