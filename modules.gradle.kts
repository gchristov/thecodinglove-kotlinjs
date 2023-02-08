includeBuild("gradle-plugins")

val projects = listOf(
    "shared/kmp-common-di",
    "shared/kmp-common-firebase",
    "shared/kmp-common-kotlin",
    "shared/kmp-common-network",
    "shared/kmp-common-test",
    "backend/appBackend",
    "backend/common-service",
    "backend/common-service-data",
    "backend/common-service-testfixtures",
    "backend/firebasefunctions",
    "backend/htmlparse",
    "backend/htmlparse-data",
    "backend/htmlparse-testfixtures",
    "backend/search",
    "backend/search-data",
    "backend/search-testfixtures",
    "backend/slack",
    "backend/slack-data",
    "backend/slack-testfixtures",
    "client/appWeb/appWeb",
)

projects.forEach { project ->
    val name = project.projectName()
    include(":$name")
    project(":$name").projectDir = java.io.File(project)
}

fun String.projectName(): String = this.split("/").last()