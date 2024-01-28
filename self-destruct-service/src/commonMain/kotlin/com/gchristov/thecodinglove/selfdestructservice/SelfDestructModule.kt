package com.gchristov.thecodinglove.selfdestructservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.slackdata.usecase.SlackSelfDestructUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SelfDestructModule : DiModule() {
    override fun name() = "self-destruct"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSelfDestructHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackSelfDestructUseCase = instance(),
                )
            }
        }
    }

    private fun provideSelfDestructHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackSelfDestructUseCase: SlackSelfDestructUseCase,
    ): SelfDestructHttpHandler = SelfDestructHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackSelfDestructUseCase = slackSelfDestructUseCase,
    )
}