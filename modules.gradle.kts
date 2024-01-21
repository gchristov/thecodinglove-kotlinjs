includeBuild("gradle-plugins")

val projects = listOf(
    "backend/appBackend",
    "backend/common-firebase-data",
    "backend/common-kotlin",
    "backend/common-network",
    "backend/common-service",
    "backend/common-service-data",
    "backend/common-service-testfixtures",
    "backend/common-test",
    "backend/html-parse-data",
    "backend/html-parse-testfixtures",
    "backend/monitoring-data",
    "backend/search",
    "backend/search-data",
    "backend/search-testfixtures",
    "backend/self-destruct",
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