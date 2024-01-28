package com.gchristov.thecodinglove.gradleplugins.common

import com.gchristov.thecodinglove.gradleplugins.Deps
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("base-node-plugin")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets.maybeCreate("commonMain").dependencies {
                    api(project(":common-kotlin"))
                    api(project(":common-test"))
                }
                sourceSets.maybeCreate("commonTest").dependencies {
                    api(Deps.Kotlin.coroutinesTest)
                }
            }
        }
    }
}