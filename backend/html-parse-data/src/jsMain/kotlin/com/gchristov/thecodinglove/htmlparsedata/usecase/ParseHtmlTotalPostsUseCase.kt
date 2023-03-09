package com.gchristov.thecodinglove.htmlparsedata.usecase

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.requireModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface ParseHtmlTotalPostsUseCase {
    suspend operator fun invoke(html: String): Either<Throwable, Int>
}

internal class RealParseHtmlTotalPostsUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
) : ParseHtmlTotalPostsUseCase {
    override suspend fun invoke(html: String): Either<Throwable, Int> = withContext(dispatcher) {
        try {
            val root = acquireRootNode(html)
            val resultsCountNode = root.querySelectorAll(TotalPostsSelector)[0]
            val count = (resultsCountNode.text as? String)?.toInt() ?: 0
            Either.Right(count)
        } catch (error: Throwable) {
            log.e(error) { error.message ?: "Error during total posts parse" }
            Either.Left(error)
        }
    }

    private suspend fun acquireRootNode(content: String): dynamic = withContext(dispatcher) {
        val htmlParser = requireModule("node-html-parser")
        htmlParser.parse(content)
    }
}

private const val TotalPostsSelector = "span[class='results-number']"