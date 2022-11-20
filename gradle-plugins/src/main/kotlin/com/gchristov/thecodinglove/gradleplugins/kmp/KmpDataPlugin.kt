package com.gchristov.thecodinglove.gradleplugins.kmp

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
open class KmpDataPlugin : KmpModulePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(project(":kmp-common-network"))
            }
        }
    }
}