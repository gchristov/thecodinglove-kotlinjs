package com.gchristov.thecodinglove.gradleplugins.kmp

import com.gchristov.thecodinglove.gradleplugins.Deps
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class ModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("platform-plugin")
        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(project(":common-kotlin"))
                implementation(project(":common-test"))
            }
            sourceSets.maybeCreate("commonTest").dependencies {
                implementation(Deps.Kotlin.coroutinesTest)
            }
        }
    }
}