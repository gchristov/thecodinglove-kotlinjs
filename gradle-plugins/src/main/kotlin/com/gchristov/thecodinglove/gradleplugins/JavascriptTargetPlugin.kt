package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
open class JavascriptBrowserTargetPlugin : KmpPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                browser {
                    commonWebpackConfig {
                        cssSupport.enabled = true
                    }
                }
            }
        }
    }
}

@Suppress("unused")
open class JavascriptNodeTargetPlugin : KmpPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                nodejs()
            }
        }
    }
}