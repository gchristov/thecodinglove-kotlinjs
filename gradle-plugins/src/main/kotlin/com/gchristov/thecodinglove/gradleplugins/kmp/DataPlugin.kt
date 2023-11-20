package com.gchristov.thecodinglove.gradleplugins.kmp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class DataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("module-plugin")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(project(":common-network"))
            }
        }
    }
}