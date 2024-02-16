package com.gchristov.thecodinglove.selfdestruct.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.selfdestruct.adapter.http.SelfDestructHttpHandler
import com.gchristov.thecodinglove.selfdestruct.adapter.slack.RealSelfDestructSlackRepository
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.SelfDestructUseCase
import com.gchristov.thecodinglove.slack.proto.http.SlackServiceRepository
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SelfDestructAdapterModule : DiModule() {
    override fun name() = "self-destruct-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSelfDestructHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    selfDestructUseCase = instance(),
                )
            }
            bindSingleton {
                provideSelfDestructSlackRepository(
                    slackServiceRepository = instance(),
                )
            }
        }
    }

    private fun provideSelfDestructHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        selfDestructUseCase: SelfDestructUseCase,
    ): SelfDestructHttpHandler = SelfDestructHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        selfDestructUseCase = selfDestructUseCase,
    )

    private fun provideSelfDestructSlackRepository(
        slackServiceRepository: SlackServiceRepository,
    ): SelfDestructSlackRepository = RealSelfDestructSlackRepository(
        slackServiceRepository = slackServiceRepository,
    )
}