package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer

expect fun providePubSubPublisher(): PubSubPublisher

expect fun providePubSubDecoder(jsonSerializer: JsonSerializer.Default): PubSubDecoder
