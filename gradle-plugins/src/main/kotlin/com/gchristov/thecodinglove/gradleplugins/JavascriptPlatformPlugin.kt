package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension

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
    plugins.apply("org.jetbrains.kotlin.js")
    extensions.configure(KotlinJsProjectExtension::class.java) {
        js(IR) {
            nodejs()
        }
    }
}

private fun Project.configureJavascriptApplication() {
    plugins.apply("dev.petuska.npm.publish")
    extensions.configure(KotlinJsProjectExtension::class.java) {
        js(IR) {
            binaries.library()
        }
    }
}

private fun Project.configureTests() {
    // Add dependencies after plugins are set to avoid missing "implementation" errors
    afterEvaluate {
        dependencies {
            add("testImplementation", kotlin("test"))
        }
    }
}