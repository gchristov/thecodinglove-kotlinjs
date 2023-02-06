includeBuild("gradle-plugins")

val projects = listOf(
    "kmp/kmp-common-di",
    "kmp/kmp-common-firebase",
    "kmp/kmp-common-kotlin",
    "kmp/kmp-common-network",
    "kmp/kmp-common-test",
    "appBackend/appBackend",
    "appBackend/common-service",
    "appBackend/common-service-data",
    "appBackend/common-service-testfixtures",
    "appBackend/firebasefunctions",
    "appBackend/htmlparse",
    "appBackend/htmlparse-data",
    "appBackend/htmlparse-testfixtures",
    "appBackend/search",
    "appBackend/search-data",
    "appBackend/search-testfixtures",
    "appBackend/slack",
    "appBackend/slack-data",
    "appBackend/slack-testfixtures",
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