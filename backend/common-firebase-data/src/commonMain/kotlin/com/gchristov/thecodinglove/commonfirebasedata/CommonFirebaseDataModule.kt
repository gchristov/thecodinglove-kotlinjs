package com.gchristov.thecodinglove.commonfirebasedata

import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object CommonFirebaseDataModule : DiModule() {
    override fun name() = "common-firebase-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideFirebaseAdmin() }
        }
    }
}
expect fun provideFirebaseAdmin(): FirebaseAdmin