package com.gchristov.thecodinglove.kmphtmlparse

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmphtmlparsedata.HtmlPost
import com.gchristov.thecodinglove.kmphtmlparsetestfixtures.HtmlCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HtmlPostParserTest {
    @Test
    fun parseTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser.parseTotalPosts(HtmlCreator.defaultHtml())
        assertEquals(
            expected = 314,
            actual = actualCount
        )
    }

    @Test
    fun parseInvalidTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser.parseTotalPosts(HtmlCreator.invalidResultsCountHtml())
        assertEquals(
            expected = 0,
            actual = actualCount
        )
    }

    @Test
    fun parsePosts() = runBlockingTest { parser ->
        val actualPosts = parser.parsePosts(HtmlCreator.defaultHtml())
        assertEquals(
            expected = ActualPostList,
            actual = actualPosts
        )
    }

    @Test
    fun parseInvalidPosts() = runBlockingTest { parser ->
        val actualPosts = parser.parsePosts(HtmlCreator.invalidHtml())
        assertEquals(
            expected = emptyList(),
            actual = actualPosts
        )
    }

    private fun runBlockingTest(testBlock: suspend (HtmlPostParser) -> Unit) =
        runTest {
            val parser = RealHtmlPostParser(FakeCoroutineDispatcher)
            testBlock(parser)
        }
}

private val ActualPostList = listOf(
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