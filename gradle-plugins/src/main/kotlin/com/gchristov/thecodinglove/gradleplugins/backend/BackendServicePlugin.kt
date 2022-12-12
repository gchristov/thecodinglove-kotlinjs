package com.gchristov.thecodinglove.gradleplugins.backend

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class BackendServicePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("kmp-data-plugin")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("jsMain").dependencies {
                implementation(project(":common-service"))
            }
        }
    }
}