package com.gchristov.thecodinglove.common.analytics.google

import arrow.core.Either
import com.gchristov.thecodinglove.common.analytics.google.model.ApiAnalyticsGoogleRequest

internal interface AnalyticsGoogleRepository {
    suspend fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>? = null,
    ): Either<Throwable, Unit>
}

internal class RealAnalyticsGoogleRepository(
    private val analyticsGoogleApi: AnalyticsGoogleApi,
) : AnalyticsGoogleRepository {
    override suspend fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>?,
    ) = try {
        val analyticsGoogleRequest = ApiAnalyticsGoogleRequest(
            clientId = clientId,
            events = listOf(
                ApiAnalyticsGoogleRequest.ApiEvent(
                    name = name,
                    params = params,
                )),
        )
        analyticsGoogleApi.sendEvent(analyticsGoogleRequest)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during analytics event${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}