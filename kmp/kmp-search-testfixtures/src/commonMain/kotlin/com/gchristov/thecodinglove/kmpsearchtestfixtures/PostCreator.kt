package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.Post

object PostCreator {
    fun defaultTotalPosts() = 7

    fun defaultPostPerPage() = 4

    fun multiPageMultiPost(): Map<Int, List<Post>> = mapOf(
        1 to listOf(
            Post(title = "p 1 i 0", url = "url", imageUrl = "imageUrl"),
            Post(title = "p 1 i 1", url = "url", imageUrl = "imageUrl"),
            Post(title = "p 1 i 2", url = "url", imageUrl = "imageUrl"),
            Post(title = "p 1 i 3", url = "url", imageUrl = "imageUrl"),
        ),
        2 to listOf(
            Post(title = "p 2 i 0", url = "url", imageUrl = "imageUrl"),
            Post(title = "p 2 i 1", url = "url", imageUrl = "imageUrl"),
            Post(title = "p 2 i 2", url = "url", imageUrl = "imageUrl"),
        )
    )

    fun singlePageSinglePost(): Map<Int, List<Post>> = mapOf(
        1 to listOf(
            Post(title = "p 1 i 0", url = "url", imageUrl = "imageUrl")
        )
    )
}