package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.domain.model.SearchPost

object SearchPostCreator {
    fun multiPageMultiPostPageSize() = 4

    fun multiPageMultiPost(): Map<Int, List<SearchPost>> = mapOf(
        1 to listOf(
            SearchPost(title = "p 1 i 0", url = "url", imageUrl = "imageUrl"),
            SearchPost(title = "p 1 i 1", url = "url", imageUrl = "imageUrl"),
            SearchPost(title = "p 1 i 2", url = "url", imageUrl = "imageUrl"),
            SearchPost(title = "p 1 i 3", url = "url", imageUrl = "imageUrl"),
        ),
        2 to listOf(
            SearchPost(title = "p 2 i 0", url = "url", imageUrl = "imageUrl"),
            SearchPost(title = "p 2 i 1", url = "url", imageUrl = "imageUrl"),
            SearchPost(title = "p 2 i 2", url = "url", imageUrl = "imageUrl"),
        )
    )

    fun multiPageMultiPostTotalCount(): Int {
        var count = 0
        for (page in multiPageMultiPost().values) {
            count += page.size
        }
        return count
    }

    fun singlePageSinglePost(): Map<Int, List<SearchPost>> = mapOf(
        1 to listOf(
            SearchPost(title = "p 1 i 0", url = "url", imageUrl = "imageUrl")
        )
    )

    fun defaultPost() = SearchPost(
        title = "post",
        url = "url",
        imageUrl = "imageUrl"
    )
}