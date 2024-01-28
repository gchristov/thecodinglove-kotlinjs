package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("com.codingfeline.buildkonfig")
        }
    }
}