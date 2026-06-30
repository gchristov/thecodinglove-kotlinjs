package com.gchristov.thecodinglove.common.kotlin

import arrow.core.Either

suspend fun <T> safeJsCall(
    errorMessage: String,
    block: suspend () -> T,
): Either<Throwable, T> = try {
    Either.Right(block())
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "$errorMessage${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
} catch (error: dynamic) {
    val jsMessage = (error.message as? String) ?: error.toString() as? String
    Either.Left(Throwable(
        message = "$errorMessage${jsMessage?.let { ": $it" } ?: ""}",
    ))
}
