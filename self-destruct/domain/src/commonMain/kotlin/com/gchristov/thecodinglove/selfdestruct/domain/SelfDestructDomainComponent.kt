package com.gchristov.thecodinglove.selfdestruct.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.RealSelfDestructUseCase
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.SelfDestructUseCase
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SelfDestructDomainComponent {
    @Provides
    @Singleton
    fun provideSelfDestructUseCase(
        log: Logger,
        selfDestructSlackRepository: SelfDestructSlackRepository,
    ): SelfDestructUseCase = RealSelfDestructUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        selfDestructSlackRepository = selfDestructSlackRepository,
    )
}
