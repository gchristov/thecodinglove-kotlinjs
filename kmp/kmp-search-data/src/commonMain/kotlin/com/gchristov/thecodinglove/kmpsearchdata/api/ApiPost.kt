package com.gchristov.thecodinglove.kmpsearchdata.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiPost(
    val title: String,
    val url: String,
    val imageUrl: String,
)