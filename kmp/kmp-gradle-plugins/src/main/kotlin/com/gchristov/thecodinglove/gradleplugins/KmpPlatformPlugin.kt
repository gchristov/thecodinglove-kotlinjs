package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class KmpPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
    }
}