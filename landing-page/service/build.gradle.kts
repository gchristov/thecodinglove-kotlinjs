import com.gchristov.thecodinglove.gradleplugins.binaryDestination

plugins {
    id("frontend-binary-plugin")
}

// Bundle resources specific to this binary
tasks.named("assemble") {
    doLast {
        copy {
            from(file(layout.projectDirectory.file("default.conf.template")))
            into(binaryDestination().get())
        }
    }
}