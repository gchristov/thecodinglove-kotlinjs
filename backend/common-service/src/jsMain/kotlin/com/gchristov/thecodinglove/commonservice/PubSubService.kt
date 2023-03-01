package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

abstract class PubSubService(
    private val pubSubServiceRegister: PubSubServiceRegister,
    private val log: Logger,
) : CoroutineScope {

    private val job = Job()

    abstract fun topic(): String

    abstract fun register()

    abstract suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit>

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForPubSubCallbacks() = pubSubServiceRegister.register(
        topic = topic(),
        callback = { message ->
            Promise { resolve, reject ->
                launch {
                    message.json?.let { log.d("Received PubSub message: json=$it") }
                    handleMessage(message).fold(
                        ifLeft = {
                            log.e(it) { it.message ?: "Error handling PubSub" }
                            reject(it)
                        },
                        ifRight = {
                            // No need to specifically ack here, as this is done automatically. More
                            // info https://stackoverflow.com/a/54996122/1589525
                            resolve(it)
                        }
                    )
                }
            }
        }
    )
}