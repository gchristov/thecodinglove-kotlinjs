package com.gchristov.thecodinglove.kmpgradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class BuildConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.codingfeline.buildkonfig")
    }
}