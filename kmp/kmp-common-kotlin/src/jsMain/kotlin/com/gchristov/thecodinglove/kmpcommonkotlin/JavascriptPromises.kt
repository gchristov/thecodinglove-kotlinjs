package com.gchristov.thecodinglove.kmpcommonkotlin

import kotlin.coroutines.*
import kotlin.js.Promise

/**
 * Simplistic Promise/Coroutine bridge, that starts a suspendable coroutine from a sync context.
 * https://discuss.kotlinlang.org/t/async-await-on-the-client-javascript/2412
 */
fun launchAsync(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) {
            if (result.isFailure) {
                println("Unhandled Promise exception: ${result.exceptionOrNull()}")
            }
        }
    })
}

/**
 * Explicitly "await" on a Node Promise<>. This just translates the Promise.then() into Kotlin
 * coroutines.
 */
suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then(
        onFulfilled = { cont.resume(it) },
        onRejected = { cont.resumeWithException(it) }
    )
}