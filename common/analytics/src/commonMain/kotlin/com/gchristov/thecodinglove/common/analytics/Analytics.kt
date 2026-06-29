package com.gchristov.thecodinglove.common.analytics

import arrow.core.getOrElse
import com.gchristov.thecodinglove.common.analytics.google.AnalyticsGoogleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface Analytics {
    fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>? = null,
    )
}

internal class RealAnalytics(
    dispatcher: CoroutineDispatcher,
    private val analyticsGoogleRepository: AnalyticsGoogleRepository,
) : Analytics {
    private val scope = CoroutineScope(dispatcher)

    override fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>?,
    ) {
        scope.launch {
            analyticsGoogleRepository.sendEvent(
                clientId = clientId,
                name = name,
                params = params,
            ).getOrElse {
                it.printStackTrace()
                return@launch
            }
        }
    }
}
