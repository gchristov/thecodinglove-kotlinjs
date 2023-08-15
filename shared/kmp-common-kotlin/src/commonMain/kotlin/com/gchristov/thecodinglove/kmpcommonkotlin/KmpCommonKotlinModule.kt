package com.gchristov.thecodinglove.kmpcommonkotlin

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object KmpCommonKotlinModule : DiModule() {
    override fun name() = "kmp-common-kotlin"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideAppConfig() }
            bindSingleton { provideLogger(appConfig = instance()) }
        }
    }

    private fun provideAppConfig(): AppConfig = AppConfig(
        logLevel = BuildConfig.APP_LOG_LEVEL,
        networkHtmlLogLevel = BuildConfig.APP_NETWORK_HTML_LOG_LEVEL,
        networkJsonLogLevel = BuildConfig.APP_NETWORK_JSON_LOG_LEVEL,
        publicUrl = BuildConfig.APP_PUBLIC_URL.removeSuffix("/"),
    )

    private fun provideLogger(appConfig: AppConfig): Logger = Logger(
        config = StaticConfig(
            minSeverity = when (appConfig.logLevel) {
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