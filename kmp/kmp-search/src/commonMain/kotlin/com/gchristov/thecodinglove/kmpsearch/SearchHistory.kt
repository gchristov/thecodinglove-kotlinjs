package com.gchristov.thecodinglove.kmpsearch

data class SearchHistory(
    val pageVisits: Map<Int, Set<Int>> = mutableMapOf()
)

internal fun SearchHistory.insert(
    postPage: Int,
    postIndexOnPage: Int,
    currentPageSize: Int
) {
    val page = (pageVisits[postPage] ?: mutableSetOf()) as MutableSet
    page.add(postIndexOnPage)
    if (page.size >= currentPageSize) {
        page.add(TerminationIndex)
    }
    (pageVisits as? MutableMap)?.put(postPage, page)
}

internal fun SearchHistory.contains(
    postPage: Int,
    postIndexOnPage: Int
): Boolean = pageVisits[postPage]?.contains(postIndexOnPage) == true

internal fun SearchHistory.getExcludedPages(): Set<Int> {
    val exclusions = mutableSetOf<Int>()
    for (key in pageVisits.keys) {
        if (pageVisits[key]?.contains(TerminationIndex) == true) {
            exclusions.add(key)
        }
    }
    return exclusions
}

internal fun SearchHistory.getExcludedPostIndexes(page: Int): Set<Int> {
    return pageVisits[page] ?: emptySet()
}

private const val TerminationIndex = -1