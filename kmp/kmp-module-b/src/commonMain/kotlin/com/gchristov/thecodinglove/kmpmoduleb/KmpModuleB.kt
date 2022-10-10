package com.gchristov.thecodinglove.kmpmoduleb

import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

class KmpModuleB {
    private val di = DI {
        bindProvider { DiTest(123) }
    }
    private val diInstance: DiTest by di.instance()

    fun name(): String {
        return "KmpModuleB" + diInstance.value
    }
}

private data class DiTest(
    val value: Int
)