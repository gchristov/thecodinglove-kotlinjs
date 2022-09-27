package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin containing common setup for the Javascript Library plugins. Should be inherited by
 * all other plugins and specifically applied to common modules exposed through [JsModulePlugin],
 * to avoid circular dependencies.
 */
open class JavascriptPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureMultiplatform()
    }
}

private fun Project.configureMultiplatform() {
    plugins.apply("org.jetbrains.kotlin.js")
}