package com.gchristov.thecodinglove.kmpsearchdata.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiSearchSession(
    val id: String,
    val query: String,
    val totalPosts: Int?,
    // Firebase doesn't like Map<Int, List<Int>> and throws ClassCastException
    val searchHistory: Map<String, List<Int>>
)