package com.gchristov.thecodinglove.slackweb.domain

import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.slackweb.domain.model.SlackConfig
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SlackWebDomainComponent {
    @Provides
    @Singleton
    fun provideSlackConfig(): SlackConfig = SlackConfig(
        clientId = BuildConfig.SLACK_CLIENT_ID,
    )
}
