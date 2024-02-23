package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class NodeModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("base-node-plugin")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets.maybeCreate("commonMain").dependencies {
                    api("com.gchristov.thecodinglove.common:kotlin")
                    api("com.gchristov.thecodinglove.common:test")
                }
                sourceSets.maybeCreate("commonTest").dependencies {
                    api(Deps.Kotlin.coroutinesTest)
                }
            }
        }
    }
}