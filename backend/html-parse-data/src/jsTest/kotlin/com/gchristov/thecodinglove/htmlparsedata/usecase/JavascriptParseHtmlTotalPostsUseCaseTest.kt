package com.gchristov.thecodinglove.htmlparsedata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparsetestfixtures.HtmlCreator
import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class JavascriptParseHtmlTotalPostsUseCaseTest {
    @Test
    fun parseTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser(HtmlCreator.defaultHtml())
        assertEquals(
            expected = Either.Right(314),
            actual = actualCount
        )
    }

    @Test
    fun parseInvalidTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser(HtmlCreator.invalidResultsCountHtml())
        assertTrue { actualCount.isLeft() }
    }

    private fun runBlockingTest(testBlock: suspend (ParseHtmlTotalPostsUseCase) -> Unit) =
        runTest {
            val parser = JavascriptParseHtmlTotalPostsUseCase(
                dispatcher = FakeCoroutineDispatcher
            )
            testBlock(parser)
        }
}