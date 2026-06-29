package com.gchristov.thecodinglove.common.analytics.google

import arrow.core.Either
import com.gchristov.thecodinglove.common.analytics.google.model.ApiAnalyticsGoogleRequest
import com.gchristov.thecodinglove.common.network.safeApiCall

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
    ) = safeApiCall("Error during analytics event") {
        analyticsGoogleApi.sendEvent(
            ApiAnalyticsGoogleRequest(
                clientId = clientId,
                events = listOf(
                    ApiAnalyticsGoogleRequest.ApiEvent(
                        name = name,
                        params = params,
                    ),
                ),
            )
        )
        Unit
    }
}
