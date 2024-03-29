package com.gchristov.thecodinglove.common.test

import co.touchlab.kermit.*

val FakeLogger = Logger(
    config = StaticConfig(
        minSeverity = Severity.Debug,
        logWriterList = listOf(TestCommonWriter())
    )
)

private class TestCommonWriter : LogWriter() {
    private val commonLogger = CommonWriter()

    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        commonLogger.log(severity, message, tag, throwable)
    }
}