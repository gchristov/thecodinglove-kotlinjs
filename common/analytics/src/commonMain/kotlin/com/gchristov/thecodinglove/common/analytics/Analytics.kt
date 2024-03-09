package com.gchristov.thecodinglove.common.analytics

import arrow.core.raise.either
import com.gchristov.thecodinglove.common.analytics.google.AnalyticsGoogleRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface Analytics {
    fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>? = null,
    )
}

internal class RealAnalytics(
    private val dispatcher: CoroutineDispatcher,
    private val analyticsGoogleRepository: AnalyticsGoogleRepository,
) : Analytics, CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    override fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>?,
    ) {
        launch(dispatcher) {
            val google = async {
                sendToGoogle(
                    clientId = clientId,
                    name = name,
                    params = params,
                )
            }
            either {
                google.await().bind()
            }.fold(
                ifLeft = {
                    it.printStackTrace()
                },
                ifRight = {
                    // No-op
                }
            )
        }
    }

    private suspend fun sendToGoogle(
        clientId: String,
        name: String,
        params: Map<String, String>?,
    ) = analyticsGoogleRepository.sendEvent(
        clientId = clientId,
        name = name,
        params = params,
    )
}
