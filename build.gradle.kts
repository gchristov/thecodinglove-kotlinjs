import groovy.json.JsonOutput

allprojects {
    repositories {
        mavenCentral()
    }
}

gradle.buildFinished {
    val packageJsonFile = File("${buildDir.path}/js/package.json")
    if (packageJsonFile.exists()) {
        val json = groovy.json.JsonSlurper().parseText(packageJsonFile.readText()) as MutableMap<String, String>
        json["main"] = "packages/thecodinglove-kmp-appJs/kotlin/thecodinglove-kmp-appJs.js"
        val prettyJson = JsonOutput.prettyPrint(JsonOutput.toJson(json))
        packageJsonFile.writeText(prettyJson)
    }
}