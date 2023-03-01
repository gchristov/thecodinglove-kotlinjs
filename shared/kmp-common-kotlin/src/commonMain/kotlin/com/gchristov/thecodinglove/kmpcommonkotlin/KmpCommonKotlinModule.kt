package com.gchristov.thecodinglove.kmpcommonkotlin

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object KmpCommonKotlinModule : DiModule() {
    override fun name() = "kmp-common-kotlin"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideLogger() }
        }
    }

    private fun provideLogger(): Logger = Logger(
        config = StaticConfig(
            minSeverity = when (BuildKonfig.APP_LOG_LEVEL) {
                "debug" -> Severity.Debug
                "verbose" -> Severity.Verbose
                "error" -> Severity.Error
                "info" -> Severity.Info
                else -> Severity.Info
            },
            logWriterList = listOf(CommonWriter())
        )
    )
}