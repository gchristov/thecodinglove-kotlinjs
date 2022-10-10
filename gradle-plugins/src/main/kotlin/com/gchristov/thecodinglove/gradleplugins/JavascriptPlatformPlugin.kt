package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class JavascriptApplicationPlugin : JavascriptPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.configureJavascriptApplication()
    }
}

@Suppress("unused")
class JavascriptLibraryPlugin : JavascriptPlatformPlugin()

abstract class JavascriptPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureJavascript()
        target.configureTests()
    }
}

private fun Project.configureJavascript() {
    plugins.apply("org.jetbrains.kotlin.multiplatform")
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        js(IR) {
            nodejs()
        }
    }
}

private fun Project.configureJavascriptApplication() {
    plugins.apply("dev.petuska.npm.publish")
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        js(IR) {
            binaries.library()
        }
    }
}

private fun Project.configureTests() {
    extensions.configure(KotlinMultiplatformExtension::class.java) {
        sourceSets.maybeCreate("commonMain").dependencies {
            implementation(kotlin("test"))
        }
    }
}