package com.gchristov.thecodinglove.searchdata.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiPost(
    val title: String,
    val url: String,
    val imageUrl: String,
)