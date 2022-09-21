package com.gchristov.thecodinglove.modulea

import com.gchristov.thecodinglove.moduleb.ModuleB

class ModuleA {
    fun name() = "ModuleA + " + ModuleB().name()
}