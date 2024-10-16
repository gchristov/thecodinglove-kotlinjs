package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Project
import org.gradle.api.file.Directory
import java.io.FileInputStream
import java.util.*

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