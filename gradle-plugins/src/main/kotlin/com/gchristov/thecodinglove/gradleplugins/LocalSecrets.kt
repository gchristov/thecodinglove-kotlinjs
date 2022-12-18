package com.gchristov.thecodinglove.gradleplugins

import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*

@Suppress("unused")
fun getLocalSecret(
    rootProject: Project,
    key: String
): String {
    val propFile = rootProject.file("./local.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    return properties.getProperty(key)
}