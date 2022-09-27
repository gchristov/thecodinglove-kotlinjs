package com.gchristov.thecodinglove.kmpgradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin containing common setup for the KMP Library plugins. Should be inherited by
 * all other plugins and specifically applied to common modules exposed through [KmpModulePlugin],
 * to avoid circular dependencies.
 */
open class KmpPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureMultiplatform()
    }
}

private fun Project.configureMultiplatform() {
    plugins.apply("org.jetbrains.kotlin.js")
}