package com.gchristov.thecodinglove.gradleplugins.kmp

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
open class KmpModulePlugin : KmpPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        // TODO: Move this to a better location
        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(project(":kmp-common-kotlin"))
                implementation(project(":kmp-common-di"))
                implementation(project(":kmp-common-test"))
            }
        }
    }
}