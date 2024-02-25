allprojects {
    repositories {
        mavenCentral()
    }
}

val taskNames = listOf("clean", "assemble", "jsTest")
taskNames.forEach { taskName ->
    tasks.register(taskName) {
        dependsOn(
            gradle.includedBuilds.map { it.task(":${taskName}All") }
        )
    }
}
