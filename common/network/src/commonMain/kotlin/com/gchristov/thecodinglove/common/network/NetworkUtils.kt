package com.gchristov.thecodinglove.common.network

import arrow.core.Either

suspend fun <T> safeApiCall(
    errorMessage: String,
    block: suspend () -> T,
): Either<Throwable, T> = try {
    Either.Right(block())
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "$errorMessage${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}
