package com.gchristov.thecodinglove.modulea

import com.gchristov.thecodinglove.moduleb.ModuleB
import org.kodein.di.DI

class ModuleA {
    fun name() = "ModuleA + " + ModuleB().name()

    fun test() {
        val kodein = DI {

        }
    }
}