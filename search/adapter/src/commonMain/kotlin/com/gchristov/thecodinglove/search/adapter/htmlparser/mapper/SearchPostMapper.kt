package com.gchristov.thecodinglove.search.adapter.htmlparser.mapper

import com.gchristov.thecodinglove.search.adapter.htmlparser.model.HtmlPost
import com.gchristov.thecodinglove.search.domain.model.SearchPost

internal fun HtmlPost.toPost() = SearchPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)