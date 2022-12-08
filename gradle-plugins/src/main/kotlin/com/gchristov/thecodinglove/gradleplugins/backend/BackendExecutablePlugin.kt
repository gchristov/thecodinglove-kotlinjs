package com.gchristov.thecodinglove.gradleplugins.backend

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class BackendExecutablePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.plugins.apply("dev.petuska.npm.publish")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                binaries.library()
                nodejs()
            }
        }
    }
}