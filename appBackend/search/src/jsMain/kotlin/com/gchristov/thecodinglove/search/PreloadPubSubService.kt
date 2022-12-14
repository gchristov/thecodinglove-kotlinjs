package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.PubSubMessage
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservice.exports
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import kotlinx.coroutines.delay

class PreloadPubSubService(
    preloadSearchResultUseCase: PreloadSearchResultUseCase
) : PubSubService() {
    override fun register() {
        exports.preloadPubSub = registerForPubSubCallbacks("trigger")
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Exception, Unit> {
        console.log("RECEIVED MESSAGE $message")
        delay(5000)
        console.log("FINISHED $message")
        return Either.Right(Unit)
    }
}