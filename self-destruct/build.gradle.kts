allprojects {
    repositories {
        mavenCentral()
    }
}

val taskNames = listOf("clean", "assemble")
taskNames.forEach {  taskName ->
    tasks.register("${taskName}All") {
        dependsOn(
            tasks.named(taskName),
            gradle.includedBuilds.map { it.task(":${taskName}All") },
            subprojects.map { it.tasks.named(taskName) },
        )
    }
}