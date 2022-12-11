package com.gchristov.thecodinglove.kmpcommondi

import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance

object DiGraph {
    @PublishedApi
    internal lateinit var di: DI
}

inline fun <reified T : Any> DiGraph.inject(): T = di.direct.instance()

fun DiGraph.insertModules(modules: List<DI.Module>) {
    di = DI.lazy {
        modules.forEach { importOnce(it) }
    }
}