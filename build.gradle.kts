/*
This file is intentionally empty as each sub-project should be independent. These tasks below allow us to execute
common actions for all projects right from the project root.
- clean, assemble and kotlinUpgradeYarnLock are used for local development
- jsTest is used to run all unit tests in one go (eg within the CI)
*/
val taskNames = listOf("clean", "assemble", "jsTest", "kotlinUpgradeYarnLock")
taskNames.forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(
            gradle.includedBuilds.map { it.task(":${taskName}All") }
        )
    }
}