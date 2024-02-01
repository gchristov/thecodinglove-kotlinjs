package com.gchristov.thecodinglove.common.adapter

import arrow.core.Either

interface Handler {
    suspend fun initialise(): Either<Throwable, Unit>
}