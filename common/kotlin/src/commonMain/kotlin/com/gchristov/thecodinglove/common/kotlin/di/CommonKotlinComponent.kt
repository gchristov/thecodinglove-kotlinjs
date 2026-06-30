package com.gchristov.thecodinglove.common.kotlin.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonKotlinComponent {
    @Provides
    @Singleton
    fun provideLogger(): Logger {
        Logger.setMinSeverity(Severity.Debug)
        Logger.setTag("Log")
        return Logger
    }

    @Provides
    @Singleton
    fun provideDefaultJsonSerializer(): JsonSerializer.Default = JsonSerializer.Default

    @Provides
    @Singleton
    fun provideExplicitNullsJsonSerializer(): JsonSerializer.ExplicitNulls = JsonSerializer.ExplicitNulls
}
