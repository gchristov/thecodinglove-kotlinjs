package com.gchristov.thecodinglove.searchdata.usecase

fun Map<Int, List<Int>>.insert(
    postPage: Int,
    postIndexOnPage: Int,
    currentPageSize: Int
) {
    val page = (this[postPage] ?: mutableListOf()) as MutableList
    page.add(postIndexOnPage)
    if (page.size >= currentPageSize) {
        page.add(TerminationIndex)
    }
    (this as? MutableMap)?.put(postPage, page)
}

fun Map<Int, List<Int>>.contains(
    postPage: Int,
    postIndexOnPage: Int
): Boolean = this[postPage]?.contains(postIndexOnPage) == true

fun Map<Int, List<Int>>.getExcludedPages(): List<Int> {
    val exclusions = mutableListOf<Int>()
    for (key in this.keys) {
        if (this[key]?.contains(TerminationIndex) == true) {
            exclusions.add(key)
        }
    }
    return exclusions
}

fun Map<Int, List<Int>>.getExcludedPostIndexes(page: Int): List<Int> {
    return this[page] ?: emptyList()
}

private const val TerminationIndex = -1