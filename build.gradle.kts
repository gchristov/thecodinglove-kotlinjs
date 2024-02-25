val taskNames = listOf("clean")
taskNames.forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(
            gradle.includedBuilds.map { it.task(":${taskName}All") }
        )
    }
}