package com.gchristov.thecodinglove.kmphtmlparser

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import org.kodein.di.DI

object HtmlParserModule : DiModule() {
    override fun name() = "kmp-htmlparser"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {

        }
    }
}