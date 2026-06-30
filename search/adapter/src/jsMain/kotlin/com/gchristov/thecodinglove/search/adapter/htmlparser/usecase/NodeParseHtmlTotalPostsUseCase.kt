package com.gchristov.thecodinglove.search.adapter.htmlparser.usecase

import com.gchristov.thecodinglove.common.kotlin.requireModule
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class NodeParseHtmlTotalPostsUseCase(
    private val dispatcher: CoroutineDispatcher,
) : ParseHtmlTotalPostsUseCase {
    override suspend fun invoke(dto: ParseHtmlTotalPostsUseCase.Dto) =
        withContext(dispatcher) {
            safeJsCall("Error during total posts parse") {
                val root = acquireRootNode(dto.html)
                val resultsCountNode = root.querySelectorAll(TotalPostsSelector)[0]
                (resultsCountNode.text as? String)?.toInt() ?: 0
            }
        }

    private suspend fun acquireRootNode(content: String): dynamic = withContext(dispatcher) {
        val htmlParser = requireModule("node-html-parser")
        htmlParser.parse(content)
    }
}

private const val TotalPostsSelector = "span[class='results-number']"
