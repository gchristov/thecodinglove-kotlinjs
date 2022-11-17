package com.gchristov.thecodinglove.kmphtmlparser

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import org.kodein.di.DI
import org.kodein.di.bindProvider

external fun require(module:String) : dynamic

object HtmlParserModule : DiModule() {
    override fun name() = "kmp-htmlparser"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { HtmlParser() }
        }
    }

    fun injectHtmlParser(): HtmlParser = inject()
}

class HtmlParser {
    fun parse(): String {
        val htmlParser = require("node-html-parser")
        val parsed = htmlParser.parse("<body><p><a>title</a></p></body>")
        val fT = parsed.firstChild.tagName
        val sT = parsed.firstChild.firstChild.tagName
        val tT = parsed.firstChild.firstChild.firstChild.tagName
        val text = parsed.firstChild.firstChild.firstChild.text
        return (fT + sT + tT + text) as String
    }
}