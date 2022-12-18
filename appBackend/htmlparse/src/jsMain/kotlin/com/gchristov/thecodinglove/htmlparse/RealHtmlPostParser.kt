package com.gchristov.thecodinglove.htmlparse

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparsedata.HtmlPost
import com.gchristov.thecodinglove.kmpcommonkotlin.requireModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class RealHtmlPostParser(private val dispatcher: CoroutineDispatcher) : HtmlPostParser {
    override suspend fun parseTotalPosts(content: String): Either<Throwable, Int> =
        withContext(dispatcher) {
            try {
                val root = acquireRootNode(content)
                val resultsCountNode = root.querySelectorAll(TotalPostsSelector)[0]
                val count = (resultsCountNode.text as? String)?.toInt() ?: 0
                Either.Right(count)
            } catch (error: Throwable) {
                Either.Left(error)
            }
        }

    override suspend fun parsePosts(content: String): Either<Throwable, List<HtmlPost>> =
        withContext(dispatcher) {
            try {
                val posts = mutableListOf<HtmlPost>()
                val root = acquireRootNode(content)
                val postNodes = root.querySelectorAll(PostSelector)
                val numPosts = postNodes.length as Int
                for (i in 0 until numPosts) {
                    val post = postNodes[i]
                    val postTitle = parsePostTitle(post)
                    val postUrl = parsePostUrl(post)
                    val postImageUrl = parsePostImageUrl(post)
                    if (postTitle != null && postUrl != null && postImageUrl != null) {
                        posts.add(
                            HtmlPost(
                                title = postTitle,
                                url = postUrl,
                                imageUrl = postImageUrl
                            )
                        )
                    }
                }
                Either.Right(posts)
            } catch (error: Throwable) {
                Either.Left(error)
            }
        }

    private suspend fun acquireRootNode(content: String): dynamic = withContext(dispatcher) {
        val htmlParser = requireModule("node-html-parser")
        htmlParser.parse(content)
    }

    private fun parsePostTitle(post: dynamic): String? {
        val anchor = post.querySelector("h1").querySelector("a")
        return anchor.text as? String
    }

    private fun parsePostUrl(post: dynamic): String? {
        val anchor = post.querySelector("h1").querySelector("a")
        return anchor.getAttribute("href") as? String
    }

    private fun parsePostImageUrl(post: dynamic): String? {
        val paragraph = post.querySelector(PostContentSelector).querySelector("p")
        val videoSource = paragraph.querySelector("video")
        if (videoSource != null) {
            val obj = videoSource.querySelector("object")
            if (obj != null) {
                return obj.getAttribute("data") as? String
            }
        }
        val imageSource = paragraph.querySelector("img")
        if (imageSource != null) {
            var src = imageSource.getAttribute("data-src")
            if (src == null) {
                src = imageSource.getAttribute("src")
            }
            return src as? String
        }
        return null
    }
}

private const val TotalPostsSelector = "span[class='results-number']"
private const val PostSelector = "article[class*='index-blog-post']"
private const val PostContentSelector = "div[class*='blog-post-content']"