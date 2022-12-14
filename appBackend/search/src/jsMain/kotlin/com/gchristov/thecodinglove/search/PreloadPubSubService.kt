package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservice.PubSubMessage
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservice.exports
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.js.Promise

class PreloadPubSubService(
    preloadSearchResultUseCase: PreloadSearchResultUseCase
) : PubSubService() {
    override fun register() {
        exports.preloadPubSub = registerForPubSubCallbacks("trigger")
    }

    override fun handleMessage(message: PubSubMessage): Promise<Unit> {
        val p = Promise { resolve, reject ->
            launch {
                console.log("RECEIVED MESSAGE $message")
                delay(5000)
                console.log("FINISHED $message")
                resolve(Unit)
            }
        }
        return p
    }
}