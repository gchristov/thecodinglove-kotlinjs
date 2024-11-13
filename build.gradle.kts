/*
This file is intentionally empty as each sub-project should be independent. These tasks below allow us to execute
common actions for all projects right from the project root.
- clean and kotlinUpgradeYarnLock are used for local development
- jsTest and assemble are used to run common tasks in one go for all services (eg within the CI)
*/
val taskNames = listOf("clean", "assemble", "jsTest", "kotlinUpgradeYarnLock")
taskNames.forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(
            gradle.includedBuilds.map { it.task(":${taskName}All") }
        )
    }
}