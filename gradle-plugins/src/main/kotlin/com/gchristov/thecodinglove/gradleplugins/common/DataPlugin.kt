package com.gchristov.thecodinglove.gradleplugins.common

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class DataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("module-plugin")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets.maybeCreate("commonMain").dependencies {
                    api(project(":common-network"))
                }
            }
        }
    }
}