/**
 * Prefix KMP modules with 'kmp-'.
 * Suffix Gradle plugin modules with '-plugins'.
 */
val projects = listOf(
    "kmp-common-di",
    "kmp-common-firebase",
    "kmp-common-network",
    "kmp-gradle-plugins",
    "kmp-module-b",
    "appApiJs",
    "appHtmlJs",
    "module-a",
)
/**
 * Javascript modules are under the 'appJs' directory.
 * KMP modules are under the 'kmp' directory.
 */
projects.forEach { project ->
    if (project.isGradlePlugin()) {
        includeBuild(project.projectDir())
    } else {
        include(":$project")
        project(":$project").projectDir = java.io.File(project.projectDir())
    }
}

fun String.projectRoot() = if (startsWith("kmp-")) "kmp" else "appJs"

fun String.projectDir() = if (projectRoot().isEmpty()) "$this" else "${projectRoot()}/$this"

fun String.isGradlePlugin() = endsWith("-plugins")