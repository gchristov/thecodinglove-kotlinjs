package com.gchristov.thecodinglove.slackweb.adapter.http

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.BaseHttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.slackweb.domain.model.SlackConfig
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackAuthRedirectHttpHandler(
    dispatcher: CoroutineDispatcher,
    jsonSerializer: JsonSerializer,
    log: Logger,
    private val slackConfig: SlackConfig,
    private val analytics: Analytics,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/slack/auth",
        contentType = ContentType.Application.Any,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = try {
        analytics.sendEvent(
            clientId = uuid4().toString(),
            name = "slack_auth_start",
        )
        response.redirect("https://slack.com/oauth/v2/authorize?client_id=${slackConfig.clientId}&scope=commands")
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during Slack auth redirect${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}