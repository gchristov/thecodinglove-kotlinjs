package com.gchristov.thecodinglove.commonkotlin

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonKotlinModule : DiModule() {
    override fun name() = "common-kotlin"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideAppConfig() }
            bindSingleton { provideLogger(appConfig = instance()) }
            bindSingleton { provideDefaultJsonSerializer() }
            bindSingleton { provideExplicitNullsJsonSerializer() }
        }
    }

    private fun provideAppConfig(): AppConfig = AppConfig(
        logLevel = BuildConfig.APP_LOG_LEVEL,
        networkHtmlLogLevel = BuildConfig.APP_NETWORK_HTML_LOG_LEVEL,
        networkJsonLogLevel = BuildConfig.APP_NETWORK_JSON_LOG_LEVEL,
        publicUrl = BuildConfig.APP_PUBLIC_URL.removeSuffix("/"),
    )

    private fun provideLogger(appConfig: AppConfig): Logger {
        val severity = when (appConfig.logLevel) {
            "debug" -> Severity.Debug
            "verbose" -> Severity.Verbose
            "error" -> Severity.Error
            "info" -> Severity.Info
            else -> Severity.Info
        }
        Logger.setMinSeverity(severity)
        Logger.setTag("Log")
        return Logger
    }

    private fun provideDefaultJsonSerializer() = JsonSerializer.Default

    private fun provideExplicitNullsJsonSerializer() = JsonSerializer.ExplicitNulls
}