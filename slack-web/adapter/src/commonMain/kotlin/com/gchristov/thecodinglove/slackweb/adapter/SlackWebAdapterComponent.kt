package com.gchristov.thecodinglove.slackweb.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.slackweb.adapter.http.SlackAuthRedirectHttpHandler
import com.gchristov.thecodinglove.slackweb.domain.model.SlackConfig
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SlackWebAdapterComponent {
    @Provides
    @Singleton
    fun provideSlackAuthRedirectHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackConfig: SlackConfig,
        analytics: Analytics,
    ): SlackAuthRedirectHttpHandler = SlackAuthRedirectHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackConfig = slackConfig,
        analytics = analytics,
    )
}
