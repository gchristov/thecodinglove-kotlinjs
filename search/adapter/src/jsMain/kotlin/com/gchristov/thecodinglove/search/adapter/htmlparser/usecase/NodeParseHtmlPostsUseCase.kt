package com.gchristov.thecodinglove.search.adapter.htmlparser.usecase

import com.gchristov.thecodinglove.common.kotlin.requireModule
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import com.gchristov.thecodinglove.search.adapter.htmlparser.model.HtmlPost
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class NodeParseHtmlPostsUseCase(
    private val dispatcher: CoroutineDispatcher,
) : ParseHtmlPostsUseCase {
    override suspend operator fun invoke(dto: ParseHtmlPostsUseCase.Dto) =
        withContext(dispatcher) {
            safeJsCall("Error during post parse") {
                val posts = mutableListOf<HtmlPost>()
                val root = acquireRootNode(dto.html)
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
                                imageUrl = postImageUrl,
                            )
                        )
                    }
                }
                posts
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

private const val PostSelector = "article[class*='index-blog-post']"
private const val PostContentSelector = "div[class*='blog-post-content']"
