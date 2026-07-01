package com.gchristov.thecodinglove.slack.service

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.CommonAnalyticsComponent
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseComponent
import com.gchristov.thecodinglove.common.kotlin.di.CommonKotlinComponent
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringComponent
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkComponent
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.pubsub.CommonPubSubComponent
import com.gchristov.thecodinglove.common.slack.CommonSlackComponent
import com.gchristov.thecodinglove.slack.adapter.SlackAdapterComponent
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSearchPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSelfDestructMessagePubSubHandler
import com.gchristov.thecodinglove.slack.domain.SlackDomainComponent
import com.gchristov.thecodinglove.slack.domain.model.Environment
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Singleton
@Component
abstract class SlackComponent(
    @get:Provides val environment: Environment,
) : CommonKotlinComponent,
    CommonNetworkComponent,
    CommonSlackComponent,
    CommonMonitoringComponent,
    CommonAnalyticsComponent,
    CommonFirebaseComponent,
    CommonPubSubComponent,
    SlackDomainComponent,
    SlackAdapterComponent {

    abstract val slackSlashCommandHttpHandler: SlackSlashCommandHttpHandler
    abstract val slackSearchPubSubHandler: SlackSearchPubSubHandler
    abstract val slackInteractivityHttpHandler: SlackInteractivityHttpHandler
    abstract val slackInteractivityPubSubHandler: SlackInteractivityPubSubHandler
    abstract val slackAuthHttpHandler: SlackAuthHttpHandler
    abstract val slackEventHttpHandler: SlackEventHttpHandler
    abstract val slackSelfDestructMessagePubSubHandler: SlackSelfDestructMessagePubSubHandler
    abstract val slackStatisticsHttpHandler: SlackStatisticsHttpHandler
    abstract val httpService: HttpService
    abstract val monitoringLogWriter: MonitoringLogWriter
    abstract val log: Logger
}
