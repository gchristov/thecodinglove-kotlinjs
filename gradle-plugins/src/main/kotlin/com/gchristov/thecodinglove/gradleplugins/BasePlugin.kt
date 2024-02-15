package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.util.prefixIfNot
import java.io.FileInputStream
import java.util.*

abstract class BaseMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
        }
    }
}

class BaseNodePlugin : BaseMultiplatformPlugin() {
    override fun apply(target: Project) {
        super.apply(target)
        target.run {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    nodejs()
                }
            }
        }
    }
}

class BaseBrowserPlugin : BaseMultiplatformPlugin() {
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
                    }
                }
            }
        }
    }
}

fun Project.binaryDestination(): Provider<Directory> {
    var currentProject = project.parent
    var name = project.name
    while (currentProject != null && currentProject != rootProject) {
        name = name.prefixIfNot("${currentProject.name}-")
        currentProject = currentProject.parent
    }
    return rootProject.layout.buildDirectory.dir("services/$name")
}

fun Project.envSecret(key: String): String {
    val propFile = file("./secrets.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    return properties.getProperty(key)
}