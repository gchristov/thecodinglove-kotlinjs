package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.api.file.Directory
import java.io.FileInputStream
import java.util.*

internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.binaryRootDirectory(): Directory = layout.buildDirectory.dir("dist/js").get()

fun Project.envSecret(key: String): String {
    val propFile = file("./secrets.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    val property = properties.getProperty(key)
    if (property.isNullOrBlank()) {
        throw IllegalStateException("Required property is missing: property=$key")
    }
    return property
}