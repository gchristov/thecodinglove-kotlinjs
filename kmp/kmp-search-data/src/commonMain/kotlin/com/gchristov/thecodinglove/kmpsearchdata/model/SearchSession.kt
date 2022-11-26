package com.gchristov.thecodinglove.kmpsearchdata.model

import com.gchristov.thecodinglove.kmpsearchdata.api.ApiSearchSession

data class SearchSession(
    val id: String,
    val query: String,
    val totalPosts: Int
)

internal fun ApiSearchSession.toSearchSession() = SearchSession(
    id = id,
    query = query,
    totalPosts = totalPosts
)