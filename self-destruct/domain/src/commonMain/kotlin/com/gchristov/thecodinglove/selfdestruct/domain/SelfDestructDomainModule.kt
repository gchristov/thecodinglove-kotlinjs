package com.gchristov.thecodinglove.selfdestruct.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.RealSelfDestructUseCase
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.SelfDestructUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object SelfDestructDomainModule : DiModule() {
    override fun name() = "self-destruct-domain"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideSelfDestructUseCase(
                    log = instance(),
                    selfDestructSlackRepository = instance(),
                )
            }
        }
    }

    private fun provideSelfDestructUseCase(
        log: Logger,
        selfDestructSlackRepository: SelfDestructSlackRepository,
    ): SelfDestructUseCase = RealSelfDestructUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        selfDestructSlackRepository = selfDestructSlackRepository,
    )
}