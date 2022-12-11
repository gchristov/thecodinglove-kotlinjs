package com.gchristov.thecodinglove.gradleplugins.kmp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KmpPlatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                nodejs()
            }
        }
    }
}