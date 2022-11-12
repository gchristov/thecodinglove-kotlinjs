/**
 * Prefix KMP modules with 'kmp-'.
 * Suffix Gradle plugin modules with '-plugins'.
 */
val projects = listOf(
    "kmp-module-b",
    "appApiJs",
    "appHtmlJs",
    "gradle-plugins",
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

fun String.projectRoot() = if (isGradlePlugin()) "" else if (startsWith("kmp-")) "kmp" else "appJs"

fun String.projectDir() = if (projectRoot().isEmpty()) "$this" else "${projectRoot()}/$this"

fun String.isGradlePlugin() = endsWith("-plugins")