package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.slack.usecase.RealVerifySlackRequestUseCase
import com.gchristov.thecodinglove.slack.usecase.VerifySlackRequestUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideVerifySlackRequestUseCase() }
            bindSingleton {
                provideSlackSlashCommandService(
                    jsonSerializer = instance(),
                    verifySlackRequestUseCase = instance()
                )
            }
        }
    }

    private fun provideVerifySlackRequestUseCase(): VerifySlackRequestUseCase =
        RealVerifySlackRequestUseCase(
            dispatcher = Dispatchers.Default
        )

    private fun provideSlackSlashCommandService(
        jsonSerializer: Json,
        verifySlackRequestUseCase: VerifySlackRequestUseCase
    ): SlackSlashCommandApiService = SlackSlashCommandApiService(
        jsonSerializer = jsonSerializer,
        verifySlackRequestUseCase = verifySlackRequestUseCase
    )
}