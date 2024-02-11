package com.gchristov.thecodinglove.search.domain.model

data class SearchConfig(
    val postsPerPage: Int,
    val preloadPubSubTopic: String,
)