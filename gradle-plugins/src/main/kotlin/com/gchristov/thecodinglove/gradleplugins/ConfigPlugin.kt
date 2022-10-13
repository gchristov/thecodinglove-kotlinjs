package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.codingfeline.buildkonfig")
    }
}