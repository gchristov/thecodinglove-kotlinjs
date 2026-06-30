package com.gchristov.thecodinglove.selfdestruct.service

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.CommonKotlinComponent
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringComponent
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkComponent
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.slack.CommonSlackComponent
import com.gchristov.thecodinglove.selfdestruct.adapter.SelfDestructAdapterComponent
import com.gchristov.thecodinglove.selfdestruct.adapter.http.SelfDestructHttpHandler
import com.gchristov.thecodinglove.selfdestruct.domain.SelfDestructDomainComponent
import com.gchristov.thecodinglove.selfdestruct.domain.model.Environment
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Singleton
@Component
abstract class SelfDestructComponent(
    @get:Provides val environment: Environment,
) : CommonKotlinComponent,
    CommonNetworkComponent,
    CommonSlackComponent,
    CommonMonitoringComponent,
    SelfDestructDomainComponent,
    SelfDestructAdapterComponent {

    abstract val httpHandler: SelfDestructHttpHandler
    abstract val httpService: HttpService
    abstract val monitoringLogWriter: MonitoringLogWriter
    abstract val log: Logger
}
