package com.gchristov.thecodinglove.kmpsearchdata.parse

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.HtmlPostParser
import com.gchristov.thecodinglove.kmpsearchdata.Post
import com.gchristov.thecodinglove.kmpsearchtestfixtures.HtmlPostCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HtmlPostParserTest {
    @Test
    fun parseTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser.parseTotalPosts(HtmlPostCreator.defaultPostListHtml())
        assertEquals(
            expected = 314,
            actual = actualCount
        )
    }

    @Test
    fun parseInvalidTotalPosts() = runBlockingTest { parser ->
        val actualCount = parser.parseTotalPosts(HtmlPostCreator.invalidResultsCountHtml())
        assertEquals(
            expected = 0,
            actual = actualCount
        )
    }

    @Test
    fun parsePosts() = runBlockingTest { parser ->
        val actualPosts = parser.parsePosts(HtmlPostCreator.defaultPostListHtml())
        assertEquals(
            expected = ActualPostList,
            actual = actualPosts
        )
    }

    @Test
    fun parseInvalidPosts() = runBlockingTest { parser ->
        val actualPosts = parser.parsePosts(HtmlPostCreator.invalidPostListHtml())
        assertEquals(
            expected = emptyList(),
            actual = actualPosts
        )
    }

    private fun runBlockingTest(testBlock: suspend (HtmlPostParser) -> Unit) =
        runTest {
            val parser = HtmlPostParser(FakeCoroutineDispatcher)
            testBlock(parser)
        }
}

private val ActualPostList = listOf(
    Post(
        title = "When the sales guy is about to click on a button I never tested",
        url = "https://thecodinglove.com/when-the-sales-guy-is-about-to-click-on-a-button-i-never-tested",
        imageUrl = "https://thecodinglove.com/content/047/6chNSPh.gif"
    ),
    Post(
        title = "When my coworker asks me if I can test his code",
        url = "https://thecodinglove.com/when-my-coworker-asks-me-if-i-can-test-his-code",
        imageUrl = "https://thecodinglove.com/content/047/hk9fPUH.gif"
    ),
    Post(
        title = "When my code goes into testing phase",
        url = "https://thecodinglove.com/when-my-code-goes-into-testing-phase",
        imageUrl = "https://thecodinglove.com/content/047/CWRslSm.gif"
    ),
    Post(
        title = "When QA starts testing my code",
        url = "https://thecodinglove.com/when-qa-starts-testing-my-code",
        imageUrl = "https://thecodinglove.com/content/047/sh8qjSF.gif"
    )
)