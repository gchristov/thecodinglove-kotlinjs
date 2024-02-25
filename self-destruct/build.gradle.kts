allprojects {
    repositories {
        mavenCentral()
    }
}

val taskNames = listOf("clean", "assemble", "jsTest", "kotlinUpgradeYarnLock")
taskNames.forEach {  taskName ->
    tasks.register("${taskName}All") {
        tasks.findByName(taskName)?.let { dependsOn(it) }
        dependsOn(gradle.includedBuilds.map { it.task(":${taskName}All") })
        subprojects.map { it.tasks.findByName(taskName)?.let { dependsOn(it) } }
    }
}