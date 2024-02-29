package com.gchristov.thecodinglove.search.adapter.htmlparser.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.requireModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class NodeParseHtmlTotalPostsUseCase(
    private val dispatcher: CoroutineDispatcher,
) : ParseHtmlTotalPostsUseCase {
    override suspend fun invoke(dto: ParseHtmlTotalPostsUseCase.Dto): Either<Throwable, Int> = withContext(dispatcher) {
        try {
            val root = acquireRootNode(dto.html)
            val resultsCountNode = root.querySelectorAll(TotalPostsSelector)[0]
            val count = (resultsCountNode.text as? String)?.toInt() ?: 0
            Either.Right(count)
        } catch (error: Throwable) {
            Either.Left(Throwable(
                message = "Error during total posts parse${error.message?.let { ": $it" } ?: ""}",
                cause = error,
            ))
        }
    }

    private suspend fun acquireRootNode(content: String): dynamic = withContext(dispatcher) {
        val htmlParser = requireModule("node-html-parser")
        htmlParser.parse(content)
    }
}

private const val TotalPostsSelector = "span[class='results-number']"