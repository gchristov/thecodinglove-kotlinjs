package com.gchristov.thecodinglove.modulea

import com.gchristov.thecodinglove.kmpmoduleb.KmpModuleB

class ModuleA {
    fun name() = "ModuleA + " + KmpModuleB().name()
}