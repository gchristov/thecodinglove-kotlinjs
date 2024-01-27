package com.gchristov.thecodinglove.searchdata.domain

data class SearchConfig(
    val postsPerPage: Int,
    val preloadPubSubTopic: String,
)