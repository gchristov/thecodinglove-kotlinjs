package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription

class FakePubSubSubscription : PubSubSubscription {
    override suspend fun initialise(
        topic: String,
        httpPath: String,
    ) = Either.Right(Unit)
}