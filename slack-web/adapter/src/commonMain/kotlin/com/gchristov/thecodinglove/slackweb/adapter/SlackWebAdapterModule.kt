package com.gchristov.thecodinglove.slackweb.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slackweb.adapter.http.SlackAuthRedirectHttpHandler
import com.gchristov.thecodinglove.slackweb.domain.model.SlackConfig
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackWebAdapterModule : DiModule() {
    override fun name() = "slack-web-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackAuthRedirectHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackConfig = instance(),
                    analytics = instance(),
                )
            }
        }
    }

    private fun provideSlackAuthRedirectHttpHandler(
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