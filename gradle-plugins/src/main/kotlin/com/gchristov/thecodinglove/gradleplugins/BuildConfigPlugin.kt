package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class BuildConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("com.codingfeline.buildkonfig")
        }
    }
}