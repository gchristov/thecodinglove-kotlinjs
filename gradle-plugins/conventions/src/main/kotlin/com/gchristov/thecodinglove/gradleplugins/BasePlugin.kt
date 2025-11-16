package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

abstract class BaseMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
        }
    }
}

class BaseNodePlugin : BaseMultiplatformPlugin() {
    @OptIn(ExperimentalDistributionDsl::class)
    override fun apply(target: Project) {
        super.apply(target)
        target.run {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    nodejs {
                        distribution {
                            outputDirectory = file("${binaryRootDirectory()}/productionExecutable")
                        }
                    }
                }
            }
        }
    }
}

class BaseBrowserPlugin : BaseMultiplatformPlugin() {
    @OptIn(ExperimentalDistributionDsl::class)
    override fun apply(target: Project) {
        super.apply(target)
        target.run {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    browser {
                        commonWebpackConfig {
                            cssSupport {
                                enabled.set(true)
                            }
                        }
                        distribution {
                            outputDirectory = file("${binaryRootDirectory()}/productionExecutable")
                        }
                    }
                }
            }
        }
    }
}