package com.gchristov.thecodinglove.gradleplugins.common

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.FileInputStream
import java.util.*

@Suppress("unused")
class BasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                js(IR) {
                    nodejs()
                }
            }
        }
    }
}

fun Project.binaryDestination(): Provider<Directory> = rootProject.layout.buildDirectory.dir("services/${project.name}")

fun Project.envVar(key: String): String {
    val propFile = file("./env.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    return properties.getProperty(key)
}