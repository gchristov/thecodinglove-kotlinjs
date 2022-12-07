includeBuild("gradle-plugins")

val projects = listOf(
    "kmp/kmp-common-di",
    "kmp/kmp-common-firebase",
    "kmp/kmp-common-kotlin",
    "kmp/kmp-common-network",
    "kmp/kmp-common-test",
    "kmp/kmp-htmlparse",
    "kmp/kmp-htmlparse-data",
    "kmp/kmp-htmlparse-testfixtures",
    "kmp/kmp-search",
    "kmp/kmp-search-data",
    "kmp/kmp-search-testfixtures",
    "appBackend/appBackend",
    "appWeb/appWeb",
)

projects.forEach { project ->
    val name = project.projectName()
    include(":$name")
    project(":$name").projectDir = java.io.File(project)
}

fun String.projectName(): String {
    val parts = this.split("/")
    if (parts.size > 1) {
        return parts[1]
    }
    return parts[0]
}