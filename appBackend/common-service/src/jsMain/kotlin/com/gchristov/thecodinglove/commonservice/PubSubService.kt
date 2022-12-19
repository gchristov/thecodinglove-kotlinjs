package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegistrations
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

    protected fun registerForPubSubCallbacks() = PubSubServiceRegistrations.register(
        topic = topic(),
        callback = { message ->
            Promise { resolve, reject ->
                launch {
                    handleMessage(message).fold(
                        ifLeft = { reject(it) },
                        ifRight = { resolve(it) }
                    )
                }
            }
        }
    )
}