package com.gchristov.thecodinglove.common.kotlin

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object CommonKotlinModule : DiModule() {
    override fun name() = "common-kotlin"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideLogger() }
            bindSingleton { provideDefaultJsonSerializer() }
            bindSingleton { provideExplicitNullsJsonSerializer() }
        }
    }

    private fun provideLogger(): Logger {
        Logger.setMinSeverity(Severity.Debug)
        Logger.setTag("Log")
        return Logger
    }

    private fun provideDefaultJsonSerializer() = JsonSerializer.Default

    private fun provideExplicitNullsJsonSerializer() = JsonSerializer.ExplicitNulls
}