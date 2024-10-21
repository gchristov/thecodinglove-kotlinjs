// This file is intentionally empty as each sub-project should be independent. These tasks allow us to build and run it
// as one from the repository root.
// TODO: Consider if this still makes sense at scale
val taskNames = listOf("clean", "assemble")
taskNames.forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(
            gradle.includedBuilds.map { it.task(":${taskName}All") }
        )
    }
}