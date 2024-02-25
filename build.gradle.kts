val taskNames = listOf("clean", "assemble")
taskNames.forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(
            gradle.includedBuilds.map { it.task(":${taskName}All") }
        )
    }
}