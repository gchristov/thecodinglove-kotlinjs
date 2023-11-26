plugins {
    id("web-executable-plugin")
}

// Copy the output binaries to their final destination
tasks.named("assemble") {
    doLast {
        copy {
            from(layout.buildDirectory.dir("distributions").get().asFile)
            into(file("$rootDir/build/bin/web"))
        }
    }
}