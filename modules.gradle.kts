/**
 * Suffix Gradle plugin modules with '-plugins'.
 */
val projects = listOf(
    "appJs",
    "modulea",
    "moduleb",
)
/**
 * Javscript modules are under the 'appJs' directory.
 */
projects.forEach { project ->
    if (project.isGradlePlugin()) {
        includeBuild(project.projectDir())
    } else {
        include(":$project")
        project(":$project").projectDir = java.io.File(project.projectDir())
    }
}

fun String.projectRoot() = "appJs"

fun String.projectDir() = "${projectRoot()}/$this"

fun String.isGradlePlugin() = endsWith("-plugins")