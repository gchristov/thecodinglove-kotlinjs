package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

abstract class BaseMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            tasks.withType<KotlinNpmInstallTask> {
                args += "--mutex network"
            }
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