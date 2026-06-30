package com.gchristov.thecodinglove.statistics.service

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.CommonKotlinComponent
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringComponent
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkComponent
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.slack.CommonSlackComponent
import com.gchristov.thecodinglove.statistics.adapter.StatisticsAdapterComponent
import com.gchristov.thecodinglove.statistics.adapter.http.StatisticsHttpHandler
import com.gchristov.thecodinglove.statistics.domain.StatisticsDomainComponent
import com.gchristov.thecodinglove.statistics.domain.model.Environment
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Singleton
@Component
abstract class StatisticsComponent(
    @get:Provides val environment: Environment,
) : CommonKotlinComponent,
    CommonNetworkComponent,
    CommonSlackComponent,
    CommonMonitoringComponent,
    StatisticsDomainComponent,
    StatisticsAdapterComponent {

    abstract val httpHandler: StatisticsHttpHandler
    abstract val httpService: HttpService
    abstract val monitoringLogWriter: MonitoringLogWriter
    abstract val log: Logger
}
