package com.gchristov.thecodinglove.kmpsearchdata.model

import com.gchristov.thecodinglove.kmpsearchdata.api.ApiSearchSession

data class SearchSession(
    val id: String,
    val query: String,
    val totalPosts: Int?,
    // Contains visited page numbers mapped to page post indexes
    val searchHistory: Map<Int, List<Int>>
)

internal fun ApiSearchSession.toSearchSession() = SearchSession(
    id = id,
    query = query,
    totalPosts = totalPosts,
    searchHistory = mutableMapOf<Int, List<Int>>().apply {
        searchHistory.keys.forEach { page ->
            searchHistory[page]?.let { itemIndex ->
                put(page.toInt(), itemIndex)
            }
        }
    }
)

internal fun SearchSession.toApiSearchSession() = ApiSearchSession(
    id = id,
    query = query,
    totalPosts = totalPosts,
    searchHistory = mutableMapOf<String, List<Int>>().apply {
        searchHistory.keys.forEach { page ->
            searchHistory[page]?.let { itemIndex ->
                put(page.toString(), itemIndex)
            }
        }
    }
)