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
            minSeverity = Severity.Debug,
            logWriterList = listOf(CommonWriter())
        )
    )
}