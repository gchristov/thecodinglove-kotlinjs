package com.gchristov.thecodinglove.htmlparsedata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparsedata.HtmlPost
import com.gchristov.thecodinglove.htmlparsetestfixtures.HtmlCreator
import com.gchristov.thecodinglove.commontest.FakeCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class JavascriptParseHtmlPostsUseCaseTest {
    @Test
    fun parsePosts() = runBlockingTest { parser ->
        val actualPosts = parser(HtmlCreator.defaultHtml())
        assertEquals(
            expected = Either.Right(TestActualPostList),
            actual = actualPosts
        )
    }

    @Test
    fun parseInvalidPosts() = runBlockingTest { parser ->
        val actualPosts = parser(HtmlCreator.invalidHtml())
        assertEquals(
            expected = Either.Right(emptyList()),
            actual = actualPosts
        )
    }

    private fun runBlockingTest(testBlock: suspend (ParseHtmlPostsUseCase) -> Unit) =
        runTest {
            val parser = JavascriptParseHtmlPostsUseCase(
                dispatcher = FakeCoroutineDispatcher
            )
            testBlock(parser)
        }
}

private val TestActualPostList = listOf(
    HtmlPost(
        title = "When the sales guy is about to click on a button I never tested",
        url = "https://thecodinglove.com/when-the-sales-guy-is-about-to-click-on-a-button-i-never-tested",
        imageUrl = "https://thecodinglove.com/content/047/6chNSPh.gif"
    ),
    HtmlPost(
        title = "When my coworker asks me if I can test his code",
        url = "https://thecodinglove.com/when-my-coworker-asks-me-if-i-can-test-his-code",
        imageUrl = "https://thecodinglove.com/content/047/hk9fPUH.gif"
    ),
    HtmlPost(
        title = "When my code goes into testing phase",
        url = "https://thecodinglove.com/when-my-code-goes-into-testing-phase",
        imageUrl = "https://thecodinglove.com/content/047/CWRslSm.gif"
    ),
    HtmlPost(
        title = "When QA starts testing my code",
        url = "https://thecodinglove.com/when-qa-starts-testing-my-code",
        imageUrl = "https://thecodinglove.com/content/047/sh8qjSF.gif"
    )
)