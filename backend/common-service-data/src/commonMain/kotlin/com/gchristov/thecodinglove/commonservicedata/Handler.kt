package com.gchristov.thecodinglove.commonservicedata

import arrow.core.Either

interface Handler {
    suspend fun initialise(): Either<Throwable, Unit>
}