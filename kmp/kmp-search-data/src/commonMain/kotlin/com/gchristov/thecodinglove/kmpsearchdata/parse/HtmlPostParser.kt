package com.gchristov.thecodinglove.kmpsearchdata.parse

import com.gchristov.thecodinglove.kmpsearchdata.Post

external fun require(module:String) : dynamic

class HtmlPostParser : PostParser {
    override fun parse(): String {
        val htmlParser = require("node-html-parser")
        val parsed = htmlParser.parse("<body><p><a>title</a></p></body>")
        val fT = parsed.firstChild.tagName
        val sT = parsed.firstChild.firstChild.tagName
        val tT = parsed.firstChild.firstChild.firstChild.tagName
        val text = parsed.firstChild.firstChild.firstChild.text
        return (fT + sT + tT + text) as String
    }

    override fun parse(content: String): List<Post> {
        TODO("Not yet implemented")
    }
}