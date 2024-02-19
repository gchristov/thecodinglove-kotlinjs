import com.gchristov.thecodinglove.gradleplugins.binaryRootDirectory

plugins {
    id("browser-binary-plugin")
}

// Bundle resources specific to this binary
tasks.named("assemble") {
    doLast {
        copy {
            from(file(layout.projectDirectory.file("default.conf.template")))
            into(binaryRootDirectory())
        }
    }
}