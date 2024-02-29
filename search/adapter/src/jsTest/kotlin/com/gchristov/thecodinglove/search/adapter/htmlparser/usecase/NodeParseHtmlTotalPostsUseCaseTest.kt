package com.gchristov.thecodinglove.search.adapter.htmlparser.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.search.testfixtures.HtmlCreator
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NodeParseHtmlTotalPostsUseCaseTest {
    @Test
    fun parseTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser(ParseHtmlTotalPostsUseCase.Dto(HtmlCreator.defaultHtml()))
        assertEquals(
            expected = Either.Right(314),
            actual = actualCount
        )
    }

    @Test
    fun parseInvalidTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser(ParseHtmlTotalPostsUseCase.Dto(HtmlCreator.invalidResultsCountHtml()))
        assertTrue { actualCount.isLeft() }
    }

    private fun runBlockingTest(testBlock: suspend (ParseHtmlTotalPostsUseCase) -> Unit) =
        runTest {
            val parser = NodeParseHtmlTotalPostsUseCase(
                dispatcher = FakeCoroutineDispatcher
            )
            testBlock(parser)
        }
}