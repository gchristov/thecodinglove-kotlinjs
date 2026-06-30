package com.gchristov.thecodinglove.slackweb.service

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.CommonAnalyticsComponent
import com.gchristov.thecodinglove.common.kotlin.di.CommonKotlinComponent
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringComponent
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkComponent
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.slack.CommonSlackComponent
import com.gchristov.thecodinglove.slackweb.adapter.SlackWebAdapterComponent
import com.gchristov.thecodinglove.slackweb.adapter.http.SlackAuthRedirectHttpHandler
import com.gchristov.thecodinglove.slackweb.domain.SlackWebDomainComponent
import me.tatarka.inject.annotations.Component

@Singleton
@Component
abstract class SlackWebComponent :
    CommonKotlinComponent,
    CommonNetworkComponent,
    CommonSlackComponent,
    CommonMonitoringComponent,
    CommonAnalyticsComponent,
    SlackWebDomainComponent,
    SlackWebAdapterComponent {

    abstract val slackAuthRedirectHttpHandler: SlackAuthRedirectHttpHandler
    abstract val httpService: HttpService
    abstract val monitoringLogWriter: MonitoringLogWriter
    abstract val log: Logger
}
