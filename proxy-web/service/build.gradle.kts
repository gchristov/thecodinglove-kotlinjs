import com.gchristov.thecodinglove.gradleplugins.binaryRootDirectory

plugins {
    alias(libs.plugins.thecodinglove.browser.binary)
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