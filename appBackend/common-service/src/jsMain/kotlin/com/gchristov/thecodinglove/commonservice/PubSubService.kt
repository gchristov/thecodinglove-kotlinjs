package com.gchristov.thecodinglove.commonservice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

abstract class PubSubService : CoroutineScope {

    private val job = Job()

    abstract fun register()

    protected abstract fun handleMessage(message: PubSubMessage): Promise<Unit>

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForPubSubCallbacks(topic: String) =
        FirebaseFunctions.pubsub.topic(topic).onPublish { handleMessage(it) }
}