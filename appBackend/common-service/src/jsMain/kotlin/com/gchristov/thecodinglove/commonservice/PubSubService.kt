package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

abstract class PubSubService : CoroutineScope {

    private val job = Job()

    abstract fun register()

    protected abstract suspend fun handleMessage(message: PubSubMessage): Either<Exception, Unit>

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForPubSubCallbacks(topic: String) =
        FirebaseFunctions.pubsub.topic(topic).onPublish {
            Promise { resolve, reject ->
                launch {
                    handleMessage(it).fold(
                        ifLeft = { reject(it) },
                        ifRight = { resolve(it) }
                    )
                }
            }
        }
}