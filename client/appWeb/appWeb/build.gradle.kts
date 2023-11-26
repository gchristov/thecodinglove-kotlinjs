plugins {
    id("web-executable-plugin")
}

// Copy the output binaries to their final destination
tasks.named("assemble") {
    doLast {
        copy {
            from(file("${layout.buildDirectory}/distributions"))
            into(file("$rootDir/build/bin/web"))
        }
    }
}