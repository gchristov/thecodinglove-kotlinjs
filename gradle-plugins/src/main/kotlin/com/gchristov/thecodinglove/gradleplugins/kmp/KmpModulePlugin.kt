package com.gchristov.thecodinglove.gradleplugins.kmp

import com.gchristov.thecodinglove.gradleplugins.Deps
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
open class KmpModulePlugin : KmpPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets.maybeCreate("commonMain").dependencies {
                implementation(project(":kmp-common-kotlin"))
                implementation(project(":kmp-common-di"))
                implementation(project(":kmp-common-test"))
            }
            sourceSets.maybeCreate("commonTest").dependencies {
                implementation(Deps.Kotlin.coroutinesTest)
            }
        }
    }
}