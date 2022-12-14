package com.gchristov.thecodinglove.commonservice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class PubSubService : CoroutineScope {

    private val job = Job()

    abstract fun register()

    protected abstract suspend fun handleMessage(message: PubSubMessage)

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForPubSubCallbacks(topic: String) =
        FirebaseFunctions.pubsub.topic(topic).onPublish { message ->
            launch {
                handleMessage(message)
            }
        }
}