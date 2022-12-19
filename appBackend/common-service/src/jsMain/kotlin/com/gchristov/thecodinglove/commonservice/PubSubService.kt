package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.api.FirebaseFunctions
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

abstract class PubSubService : CoroutineScope {

    private val job = Job()

    abstract fun topic(): String

    abstract fun register()

    protected abstract suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit>

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForPubSubCallbacks() =
        FirebaseFunctions.pubsub.topic(topic()).onPublish {
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