plugins {
    id("web-executable-plugin")
}

// Copy the output binaries to their final destination
tasks.named("assemble") {
    doLast {
        val sourceDir = file("$buildDir/distributions")
        val destinationDir = file("$rootDir/build/bin/web")

        copy {
            from(sourceDir)
            into(destinationDir)
        }
    }
}