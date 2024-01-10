plugins {
    id("web-executable-plugin")
}

// Copy the output binaries to their final destination.
// Currently, that is the /docker/bin directory.
tasks.named("assemble") {
    doLast {
        copy {
            from(layout.buildDirectory.dir("dist/js/productionExecutable").get().asFile)
            into(file("$rootDir/docker/bin/web"))
        }
    }
}