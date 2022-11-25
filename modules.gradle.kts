/**
 * Prefix KMP modules with 'kmp-'.
 * Suffix Gradle plugin modules with '-plugins'.
 */
val projects = listOf(
    "kmp-common-di",
    "kmp-common-firebase",
    "kmp-common-kotlin",
    "kmp-common-network",
    "kmp-common-test",
    "kmp-htmlparse",
    "kmp-htmlparse-data",
    "kmp-htmlparse-testfixtures",
    "kmp-search",
    "kmp-search-data",
    "kmp-search-testfixtures",
    "appApiJs",
    "appHtmlJs",
    "gradle-plugins",
)
/**
 * Javascript modules are under the 'appJs' directory.
 * KMP modules are under the 'kmp' directory..
 */
projects.forEach { project ->
    if (project.isGradlePlugin()) {
        includeBuild(project.projectDir())
    } else {
        include(":$project")
        project(":$project").projectDir = java.io.File(project.projectDir())
    }
}

fun String.projectRoot() = if (startsWith("kmp-")) "kmp" else if (isGradlePlugin()) "" else "appJs"

fun String.projectDir() = if (projectRoot().isEmpty()) "$this" else "${projectRoot()}/$this"

fun String.isGradlePlugin() = endsWith("-plugins")