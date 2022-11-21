package com.gchristov.thecodinglove.kmpsearchdata.parse

import com.gchristov.thecodinglove.kmpsearchdata.Post

interface PostParser {
    fun parse(): String
    fun parse(content: String): List<Post>
}