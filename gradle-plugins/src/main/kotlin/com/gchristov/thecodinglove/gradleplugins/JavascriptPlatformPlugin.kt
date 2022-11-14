package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class JavascriptBrowserExecutablePlugin : KmpPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                binaries.executable()
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
class JavascriptNodeExecutablePlugin : JavascriptNodeModulePlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.plugins.apply("dev.petuska.npm.publish")
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                binaries.library()
            }
        }
    }
}

@Suppress("unused")
open class JavascriptNodePlatformPlugin : KmpPlatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(IR) {
                nodejs()
            }
        }
    }
}